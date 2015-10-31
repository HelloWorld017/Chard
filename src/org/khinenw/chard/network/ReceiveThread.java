package org.khinenw.chard.network;

import org.khinenw.chard.ChardServer;

public class ReceiveThread extends Thread{
	public void run(){
		ChardServer.getNetwork().sessions.forEach((k, v) -> {
			
			v.getSocket().read(dst)
		});
	}
}
