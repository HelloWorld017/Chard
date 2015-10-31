package org.khinenw.chard;

import java.util.ArrayList;
import java.util.List;

import org.khinenw.chard.event.Event;
import org.khinenw.chard.event.Listener;
import org.khinenw.chard.network.Network;
import org.khinenw.chard.utils.Configuration;
import org.khinenw.chard.utils.Logger;
import org.khinenw.chard.utils.Logger.LogLevel;

public class ChardServer {
	private List<Listener> handlerList = new ArrayList<>();
	private Configuration serverConfiguration;
	private static ChardServer instance;
	private static Network network;
	
	public ChardServer(){
		instance = this;
		serverConfiguration = new Configuration("server.conf", "default.conf");
		try{
			network = new Network(this, serverConfiguration.getInt("port"));
		}catch(Exception e){
			this.log(e, LogLevel.CRITICAL);
		}
	}
	
	public void log(Throwable t, Logger.LogLevel level){
		t.printStackTrace();
	}
	
	public void log(String s, Logger.LogLevel level){
		System.out.println(s);
	}
	
	public String getTranslation(String translationKey, String ...args){
		return translationKey;
	}
	
	public Configuration getConfiguration(){
		return serverConfiguration;
	}
	
	public static Network getNetwork(){
		return network;
	}
	
	public void callEvent(Event e){
		handlerList.forEach((v) -> {
			v.onEvent(e);
		});
	}

	public static ChardServer getInstance(){
		return instance;
	}
}
