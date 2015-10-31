package org.khinenw.chard.network;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.khinenw.chard.ChardServer;
import org.khinenw.chard.network.packet.Packet;
import org.khinenw.chard.network.packet.PingPacket;
import org.khinenw.chard.network.packet.PongPacket;
import org.khinenw.chard.utils.Logger.LogLevel;

public class Session {
	private SocketChannel socket;
	
	public Session(SocketChannel socket){
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
			return;
		}
		
		pk.buffer = buffer;
		try{
			pk.decode();
		}catch(Exception e){
			ChardServer.getInstance().log(e, LogLevel.DEBUG);
			return;
		}
		
		handlePacket(pk);
	}
	
	public void handlePacket(Packet pk){
		if(pk instanceof PingPacket){
			PongPacket reply = new PongPacket();
			reply.sendTime = System.currentTimeMillis();
			reply.pingId = ((PingPacket) pk).pingId;
			reply.encode();
			
			sendPacket(reply);
			ChardServer.getInstance().log("SENT PONG TO " + this.getAddress().toString(), LogLevel.DEBUG);
			return;
		}
	}

	public boolean sendPacket(Packet pk){
		try{
			socket.write(pk.payload);
			return true;
		}catch(IOException e){
			ChardServer.getInstance().log(e, LogLevel.WARNING);
			return false;
		}
	}
}
