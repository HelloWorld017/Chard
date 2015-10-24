package org.khinenw.chard.network;

import java.net.DatagramPacket;
import java.util.Map;
import java.util.HashMap;

public class Network {
	private Map<String, Packet> registeredPacket = new HashMap<>();
	
	
	public void readPacket(DatagramPacket pk){
		
	}
	
	public void registerPacket(String id, Packet pk){
		registeredPacket.put(id, pk);
	}
}
