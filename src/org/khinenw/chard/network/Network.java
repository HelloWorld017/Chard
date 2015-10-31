package org.khinenw.chard.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.khinenw.chard.ChardServer;
import org.khinenw.chard.network.packet.Packet;
import org.khinenw.chard.network.packet.PacketInfo;
import org.khinenw.chard.network.packet.PingPacket;
import org.khinenw.chard.network.packet.PongPacket;
import org.khinenw.chard.utils.Logger.LogLevel;

public class Network {
	private Map<Integer, Class<? extends Packet>> registeredPacket = new HashMap<>();
	private Map<String, Integer> ipSec = new TreeMap<>();
	private Map<String, Integer> blockList = new TreeMap<>();
	private Map<String, Session> sessions = new HashMap<>();
	private ChardServer server;
	private DatagramSocket socket;
	private NetworkTickThread tickThread;
	private PacketReceiveThread receiveThread;
	
	public static final int MAX_BLOCK_COUNT = 1000;
	public static final int BLOCK_TIME = 600;
	
	public Network(ChardServer server, int port){
		ChardServer.getInstance().log("CHARD-NET STARTED. RECEIVING PACKET FROM " + port, LogLevel.INFO);
		this.server = server;
		registerDefaultPackets();
		try{
			socket = new DatagramSocket(port);
		}catch(Exception e){
			server.log(e, LogLevel.EMERGENCY);
		}
		tickThread = new NetworkTickThread();
		receiveThread = new PacketReceiveThread(socket);
		
		tickThread.start();
		receiveThread.start();
	}
	
	public void registerDefaultPackets(){
		registerPacket(PacketInfo.PING.ID, PingPacket.class);
		registerPacket(PacketInfo.PONG.ID, PongPacket.class);
	}
	
	public void tick(){
		blockList.forEach((k, v) -> {
			if(v - 1 <= 0){
				blockList.remove(k);
			}else{
				blockList.replace(k, v - 1);
			}
		});

		ipSec.forEach((k, v) -> {
			if(v >= MAX_BLOCK_COUNT){
				blockList.put(k, BLOCK_TIME);
				server.log("Blocked " + k + " for " + BLOCK_TIME + "seconds.", LogLevel.INFO);
			}
		});
		
		ipSec.clear();
	}
	
	public void receivePacket(DatagramPacket datagramPacket){
		String host = datagramPacket.getAddress().getCanonicalHostName();
		server.log("PACKET RECEIVED FROM " + host, LogLevel.DEBUG);
		if(blockList.containsKey(host)){
			server.log("BLOCKED PACKET FROM " + host, LogLevel.DEBUG);
			return;
		}
		
		if(ipSec.containsKey(host)){
			ipSec.replace(host, ipSec.get(host) + 1);
		}else{
			ipSec.put(host, 1);
		}
		Packet pk = this.getPacket(datagramPacket.getData()[0]);
		if(pk == null){
			server.log("UNKNOWN PACKET FROM " + host, LogLevel.DEBUG);
			return;
		}
		
		pk.sourceAddress = datagramPacket.getAddress();
		pk.sourcePort = datagramPacket.getPort();
		pk.buffer = datagramPacket.getData();
		try{
			pk.decode();
		}catch(Exception e){
			server.log(e, LogLevel.DEBUG);
			return;
		}
		
		String source = host + ":" + datagramPacket.getPort();
		
		if(pk instanceof PingPacket){
			PongPacket reply = new PongPacket();
			reply.sendTime = System.currentTimeMillis();
			reply.pingId = ((PingPacket) pk).pingId;
			reply.encode();
			
			sendPacket(reply, pk.sourceAddress, pk.sourcePort);
			server.log("SENT PONG TO " + source, LogLevel.DEBUG);
			return;
		}
		
		if(!sessions.containsKey(source)){
			server.log("CREATING NEW SESSION", LogLevel.DEBUG);
			sessions.put(source, new Session(datagramPacket.getAddress(), datagramPacket.getPort()));
		}
		sessions.get(source).handlePacket(pk);
	}
	
	public void registerPacket(int id, Class<? extends Packet> pk){
		registeredPacket.put(id, pk);
	}
	
	public boolean sendPacket(DatagramPacket pk){
		try{
			socket.send(pk);
			return true;
		}catch(IOException e){
			server.log(e, LogLevel.WARNING);
			return false;
		}
	}
	
	public boolean sendPacket(Packet pk, InetAddress addr, int port){
		DatagramPacket datagramPk = new DatagramPacket(pk.buffer, pk.buffer.length, addr, port);
		return sendPacket(datagramPk);
	}
	
	public boolean sendPacket(Packet pk, SocketAddress addr){
		DatagramPacket datagramPk = new DatagramPacket(pk.buffer, pk.buffer.length, addr);
		return sendPacket(datagramPk);
	}
	
	public Packet getPacket(int id){
		Class<? extends Packet> clazz = registeredPacket.get(id);
		if(clazz != null){
			try{
				return clazz.newInstance();
			}catch(Exception e){
				server.log(e, LogLevel.CRITICAL);
			}
		}
		
		return null;
	}
	
	public void close(){
		this.tickThread.setCancelled();
		this.receiveThread.setCancelled();
	}
}
