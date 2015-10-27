package org.khinenw.chard.network;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.Map;
import java.util.TreeMap;

import org.khinenw.chard.ChardServer;
import org.khinenw.chard.network.packet.Packet;
import org.khinenw.chard.utils.Logger.LogLevel;

import java.util.HashMap;

public class Network {
	private Map<Integer, Class<? extends Packet>> registeredPacket = new HashMap<>();
	private Map<String, Integer> ipSec = new TreeMap<>();
	private Map<String, Integer> blockList = new TreeMap<>();
	private Map<String, Session> sessions = new HashMap<>();
	private ChardServer server;
	
	public static final int MAX_BLOCK_COUNT = 1000;
	public static final int BLOCK_TIME = 600;
	
	public Network(ChardServer server, InetAddress addr, short port){
		this.server = server;
		try{
			DatagramChannel channel = DatagramChannel.open();
			channel.configureBlocking(false);
			channel.socket().bind(new InetSocketAddress(addr, port));
		}catch(Exception e){
			server.log(e, LogLevel.EMERGENCY);
		}
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
		if(blockList.containsKey(host)) return;
		
		if(ipSec.containsKey(host)){
			ipSec.replace(host, ipSec.get(host) + 1);
		}else{
			ipSec.put(host, 1);
		}
		
		String source = host + ":" + datagramPacket.getPort();
		if(!sessions.containsKey(source)) sessions.put(source, new Session(datagramPacket.getAddress(), (short) datagramPacket.getPort()));
		Packet pk = this.getPacket(datagramPacket.getData()[0]);
		pk.source = datagramPacket.getSocketAddress();
		pk.buffer = datagramPacket.getData();
		pk.decode();
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
}
