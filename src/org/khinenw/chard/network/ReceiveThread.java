package org.khinenw.chard.network;

import java.nio.ByteBuffer;

import org.khinenw.chard.ChardServer;
import org.khinenw.chard.utils.Logger.LogLevel;

public class ReceiveThread extends Thread{
	private boolean isCancelled = false;
	private ByteBuffer readBuffer;
	
	public void setCancelled(boolean isCancelled){
		this.isCancelled = isCancelled;
	}
	
	public void setCancelled(){
		setCancelled(true);
	}
	
	
	public void run(){
		readBuffer = ByteBuffer.allocate(65535);
		while(!isCancelled){
			ChardServer.getNetwork().sessions.forEach((k, v) -> {
				try{
					if(v.getSocket().isConnected()){
						v.getSocket().read(readBuffer);
						if(readBuffer.position() != 0){
							readBuffer.clear();
							v.receivePacket(readBuffer);
							readBuffer = ByteBuffer.allocate(65535);
						}
					}
				}catch(Exception e){
					ChardServer.getNetwork().closeSession(k);
				}
			});
			try{
				Thread.sleep(50);
			}catch(InterruptedException e){
				ChardServer.getInstance().log(e, LogLevel.CRITICAL);
			}
		}
		
		ChardServer.getNetwork().sessions.forEach((k, v) -> {
				v.close();
		});
	}
}
