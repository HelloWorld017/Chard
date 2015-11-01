package org.khinenw.chard.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.TreeMap;

import org.khinenw.chard.ChardPlayer;
import org.khinenw.chard.ChardServer;
import org.khinenw.chard.event.PacketIncomeEvent;
import org.khinenw.chard.network.packet.LoginPacket;
import org.khinenw.chard.network.packet.LoginReplyPacket;
import org.khinenw.chard.network.packet.LoginRequestPacket;
import org.khinenw.chard.network.packet.Packet;
import org.khinenw.chard.network.packet.PingPacket;
import org.khinenw.chard.network.packet.PongPacket;
import org.khinenw.chard.network.packet.ResultStatusPacket;
import org.khinenw.chard.network.packet.SplitablePacket;
import org.khinenw.chard.utils.EncryptionHelper;
import org.khinenw.chard.utils.Logger.LogLevel;

public class Session {
	private SocketChannel socket;
	private TreeMap<Short, SplitablePacket> splitQueue;
	private PrivateKey loginPrivKey;
	
	public static final short MAX_SPLIT_SIZE = 128;
	public static final int MAX_SPLIT_QUEUE = 256;
	
	public Session(SocketChannel socket){
		this.splitQueue = new TreeMap<>();
		this.socket = socket;
		try{
			socket.configureBlocking(false);
		}catch(IOException e){
			ChardServer.getInstance().log(e, LogLevel.CRITICAL);
		}
	}	
	
	public InetAddress getAddress(){
		return socket.socket().getInetAddress();
	}
	
	public int getPort(){
		return socket.socket().getPort();
	}
	
	public SocketChannel getSocket(){
		return this.socket;
	}
	
	public void receivePacket(ByteBuffer packet){
		String host = getAddress().getCanonicalHostName();
		ChardServer.getInstance().log("PACKET RECEIVED FROM " + host, LogLevel.DEBUG);
		
		if(ChardServer.getNetwork().isBlock(host)) return;
		
		byte[] buffer = packet.array();
		Packet pk = ChardServer.getNetwork().getPacket(buffer[0]);
		if(pk == null){
			ChardServer.getInstance().log("UNKNOWN PACKET FROM " + host, LogLevel.DEBUG);
			
			sendResult(ResultStatusPacket.FAIL_WRONG_DATA);
			return;
		}
		
		pk.buffer = buffer;
		try{
			pk.decode();
		}catch(Exception e){
			ChardServer.getInstance().log(e, LogLevel.DEBUG);
			
			sendResult(ResultStatusPacket.FAIL_WRONG_DATA);
			return;
		}
		
		handlePacket(pk);
	}
	
	public void handlePacket(Packet pk){
		
		PacketIncomeEvent event = new PacketIncomeEvent(pk);
		ChardServer.getInstance().callEvent(event);
		if(event.isCancelled()){
			return;
		}
		
		if(pk instanceof PingPacket){
			PongPacket reply = new PongPacket();
			reply.sendTime = System.currentTimeMillis();
			reply.pingId = ((PingPacket) pk).pingId;
			reply.encode();
			
			if(sendPacket(reply)){
				ChardServer.getInstance().log("SENT PONG TO " + this.getAddress().toString(), LogLevel.DEBUG);
			}else{
				ChardServer.getInstance().log("COULD'T SEND A PACKET!", LogLevel.WARNING);
				sendResult(ResultStatusPacket.FAIL_SERVER_FAULT);
			}
			return;
		}else if(pk instanceof LoginRequestPacket){
			try{
				KeyPair pair = EncryptionHelper.generateKey();
				loginPrivKey = pair.getPrivate();
				
				LoginReplyPacket reply = new LoginReplyPacket();
				reply.publicKey = pair.getPublic();
				reply.encode();
				
				pair = null;
			}catch(Exception e){
				ChardServer.getInstance().log(e, LogLevel.WARNING);
				sendResult(ResultStatusPacket.FAIL_SERVER_FAULT);
			}
		}else if(pk instanceof LoginPacket){
			String name = ((LoginPacket) pk).name;
			String pw;
			try{
				pw = EncryptionHelper.hash(EncryptionHelper.decrypt(((LoginPacket) pk).pw, this.loginPrivKey));
			}catch(Exception e){
				ChardServer.getInstance().log(e, LogLevel.WARNING);
				sendResult(ResultStatusPacket.FAIL_SERVER_FAULT);
				return;
			}
			
			ChardPlayer player = new ChardPlayer(name, this);
			if(!player.authenticate(pw)){
				sendResult(ResultStatusPacket.FAIL_WRONG_DATA);
				return;
			}
			
			if(ChardServer.getInstance().getPlayerById(name) != null){
				ChardServer.getInstance().getPlayerById(name).kick("reason.multi-login");
				sendResult(ResultStatusPacket.FAIL_UNKNOWN, "reason.multi-login");
				return;
			}
			
			ChardServer.getInstance().registerOnlinePlayer(player);
		}
	}

	public void handleSplit(SplitablePacket pk){
		if(!pk.hasSplit) return;
	
		if(splitQueue.size() >= MAX_SPLIT_QUEUE) return;
		if(pk.splitCount >= MAX_SPLIT_SIZE) return;
			
		splitQueue.put(pk.splitIndex, pk);
		
		if(splitQueue.size() >= pk.splitCount){
			SplitablePacket newPk = (SplitablePacket) ChardServer.getNetwork().getPacket(pk.getID());
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			baos.write(pk.getID());
			baos.write((byte) 0x00); //no split 
			splitQueue.forEach((k, v) -> {
				//int ID(1) + boolean hasSplit(1) + short splitIndex(2) + short splitCount(2)
				try{
					baos.write(Arrays.copyOfRange(v.buffer, 6, v.buffer.length));
				}catch(Exception e){
					ChardServer.getInstance().log(e, LogLevel.WARNING);
				}
			});
			
			newPk.buffer = baos.toByteArray();
			newPk.decode();
			handlePacket(newPk);
		}
	}
	
	public boolean sendPacket(Packet pk){
		try{
			pk.payload.flip();
			socket.write(pk.payload);
			return true;
		}catch(IOException e){
			ChardServer.getInstance().log(e, LogLevel.WARNING);
			return false;
		}
	}
	
	public void sendSplitable(SplitablePacket pk, int splitId){
		int len = pk.buffer.length;
		short splitCount = (short) Math.ceil(len / 8000);
		SplitablePacket original = (SplitablePacket) ChardServer.getNetwork().getPacket(pk.getID());
		for(short i = 0; i < splitCount; i++){
			SplitablePacket spk = (SplitablePacket) original.clone();
			spk.buffer = Arrays.copyOfRange(pk.buffer, i * 8000, (i + 1) * 8000);
			spk.hasSplit = true;
			spk.splitCount = splitCount;
			spk.splitIndex = i;
			sendPacket(spk);
		}
	}
	
	public void sendResult(int statusCode){
		sendResult(statusCode, "");
	}
	
	public void sendResult(int statusCode, String message){
		ResultStatusPacket res = new ResultStatusPacket();
		res.status = statusCode;
		res.message = message;
		res.encode();
		sendPacket(res);
	}
	
	public void close(){
		if(socket.isOpen()){
			try{
				socket.close();
			}catch(Exception e){
				ChardServer.getInstance().log(e, LogLevel.WARNING);
			}
		}
	}
}
