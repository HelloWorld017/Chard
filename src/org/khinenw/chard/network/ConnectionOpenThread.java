package org.khinenw.chard.network;

import java.nio.channels.ServerSocketChannel;

import org.khinenw.chard.ChardServer;
import org.khinenw.chard.utils.Logger.LogLevel;

public class ConnectionOpenThread extends Thread{
	private ServerSocketChannel socket;
	private boolean isCancelled = false;
	
	public ConnectionOpenThread(ServerSocketChannel socket){
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
				ChardServer.getNetwork().createSession(socket.accept());
			}catch(Exception e){
				ChardServer.getInstance().log(e, LogLevel.CRITICAL);
			}
		}
	}
}
