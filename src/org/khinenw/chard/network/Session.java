package org.khinenw.chard.network;

import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.khinenw.chard.ChardServer;
import org.khinenw.chard.network.packet.EncapsulatedPacket;
import org.khinenw.chard.network.packet.Packet;
import org.khinenw.chard.utils.Logger.LogLevel;

public class Session {
	private InetAddress address;
	private short port;
	private Map<Integer, Map<Short, Packet>> splitQueue = new HashMap<>();
	
	public static final int MAX_SPLIT_QUEUE = 256;
	public static final short MAX_SPLIT_SIZE = 128;
	
	public Session(InetAddress addr, short port){
		
	}
	
	public void handlePacket(Packet pk){
		if(pk instanceof EncapsulatedPacket && ((EncapsulatedPacket) pk).hasSplit){
			handleSplit((EncapsulatedPacket) pk);
			return;
		}
	}
	
	public void handleSplit(EncapsulatedPacket pk){
		if(!pk.hasSplit) return;
		if(!splitQueue.containsKey(pk.splitId)){
			if(splitQueue.size() >= MAX_SPLIT_QUEUE) return;
			if(pk.splitCount >= MAX_SPLIT_SIZE) return;
			
			splitQueue.put(pk.splitId, new TreeMap<>());
		}
		
		splitQueue.get(pk.splitId).put(pk.splitIndex, pk);
		
		if(splitQueue.get(pk.splitId).size() >= pk.splitCount){
			EncapsulatedPacket newPk = (EncapsulatedPacket) ChardServer.getInstance().getNetwork().getPacket(pk.getID());
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			baos.write(pk.getID());
			baos.write((byte) 0x00); //no split 
			splitQueue.get(pk.splitId).forEach((k, v) -> {
				//int ID(1) + boolean hasSplit(1) + int splitId(4) + short splitIndex(2) + short splitCount(2)
				try{
					baos.write(Arrays.copyOfRange(v.buffer, 10, v.buffer.length));
				}catch(Exception e){
					ChardServer.getInstance().log(e, LogLevel.WARNING);
				}
			});
			
			newPk.buffer = baos.toByteArray();
			newPk.decode();
			handlePacket(newPk);
		}
	}
}
