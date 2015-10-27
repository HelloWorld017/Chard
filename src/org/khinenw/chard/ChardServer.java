package org.khinenw.chard;

import java.util.ArrayList;
import java.util.List;

import org.khinenw.chard.event.Event;
import org.khinenw.chard.event.Listener;
import org.khinenw.chard.network.Network;
import org.khinenw.chard.utils.Logger;

public class ChardServer {
	private List<Listener> handlerList = new ArrayList<>();
	public void log(Throwable t, Logger.LogLevel level){
		
	}
	
	public void log(String s, Logger.LogLevel level){
		
	}
	
	public String getTranslation(String translationKey, String ...args){
		return translationKey;
	}
	
	public Network getNetwork(){
		return null;
	}
	
	public void callEvent(Event e){
		handlerList.forEach((v) -> {
			v.onEvent(e);
		});
	}

	public static ChardServer getInstance(){
		return null;
	}
}
