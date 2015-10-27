package org.khinenw.chard.network;

import org.khinenw.chard.ChardServer;
import org.khinenw.chard.utils.Logger.LogLevel;

public class NetworkTickThread extends Thread{
	private boolean isCancelled = false;
	
	public void setCancelled(boolean isCancelled){
		this.isCancelled = isCancelled;
	}
	
	public void setCancelled(){
		setCancelled(true);
	}
	
	public void onRun(){
		while(!isCancelled){
			try{
				ChardServer.getInstance().getNetwork().tick();
				Thread.sleep(1000);
			}catch(Exception e){
				ChardServer.getInstance().log(e, LogLevel.CRITICAL);
			}
		}
	}
}
