package org.khinenw.chard.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.khinenw.chard.ChardServer;
import org.khinenw.chard.utils.Logger.LogLevel;

public class PacketReceiveThread extends Thread{
	private DatagramSocket socket;
	private boolean isCancelled = false;
	
	public PacketReceiveThread(DatagramSocket socket){
		this.socket = socket;
	}
	
	public void setCancelled(boolean isCancelled){
		this.isCancelled = isCancelled;
	}
	
	public void setCancelled(){
		setCancelled(true);
	}
	
	public void run(){
		while(!isCancelled){
			try{
				byte[] buffer = new byte[65535];
				DatagramPacket pk = new DatagramPacket(buffer, buffer.length);
				socket.receive(pk);
				ChardServer.getNetwork().receivePacket(pk);
			}catch(Exception e){
				ChardServer.getInstance().log(e, LogLevel.CRITICAL);
			}
		}
	}
}
