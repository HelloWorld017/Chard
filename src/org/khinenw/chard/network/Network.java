package org.khinenw.chard.network;

import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
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
	public Map<String, Session> sessions = new HashMap<>();
	private ChardServer server;
	private ServerSocketChannel serverSocket;
	private NetworkTickThread tickThread;
	private ConnectionOpenThread openThread;
	
	public static final int MAX_BLOCK_COUNT = 1000;
	public static final int BLOCK_TIME = 600;
	
	public Network(ChardServer server, int port){
		ChardServer.getInstance().log("CHARD-NET STARTED. RECEIVING PACKET FROM " + port, LogLevel.INFO);
		this.server = server;
		registerDefaultPackets();
		try{
			serverSocket = ServerSocketChannel.open();
			serverSocket.socket().bind(new InetSocketAddress(port));
		}catch(Exception e){
			server.log(e, LogLevel.EMERGENCY);
		}
		
		tickThread = new NetworkTickThread();
		openThread = new ConnectionOpenThread(serverSocket);
		
		tickThread.start();
		openThread.start();
	}
	
	public void registerDefaultPackets(){
		registerPacket(PacketInfo.PING.ID, PingPacket.class);
		registerPacket(PacketInfo.PONG.ID, PongPacket.class);
	}
	
	public void createSession(SocketChannel socket){
		sessions.put(socket.socket().getInetAddress() + ":" + socket.socket().getPort(), new Session(socket));
	}
	
	public void tick(){
		blockList.forEach((k, v) -> {
			if(v - 1 <= 0){
				blockList.remove(k);
			}else{
				blockList.replace(k, v - 1);
			}
		});

		ipSec.clear();
	}
	
	public boolean isBlock(String host){
		if(blockList.containsKey(host)){
			server.log("BLOCKED PACKET FROM " + host, LogLevel.DEBUG);
			return true;
		}
		
		if(ipSec.containsKey(host)){
			int receiveCount = ipSec.get(host);
			
			if(receiveCount >= MAX_BLOCK_COUNT){
				blockList.put(host, BLOCK_TIME);
				server.log("Blocked " + host + " for " + BLOCK_TIME + "seconds.", LogLevel.INFO);
				return true;
			}
			
			ipSec.replace(host, receiveCount + 1);
			
		}else{
			ipSec.put(host, 1);
		}
		
		return false;
	}
	
	public void registerPacket(int id, Class<? extends Packet> pk){
		registeredPacket.put(id, pk);
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
		this.openThread.setCancelled();
	}
}
