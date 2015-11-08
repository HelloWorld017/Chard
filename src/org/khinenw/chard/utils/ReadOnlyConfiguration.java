package org.khinenw.chard.utils;

import java.util.HashMap;
import java.util.Map;

import org.khinenw.chard.ChardServer;
import org.khinenw.chard.utils.Logger.LogLevel;

public class ReadOnlyConfiguration extends Configuration{

	public ReadOnlyConfiguration(Map<String, String> defaultConf){
		super("", defaultConf);
	}
	
	public ReadOnlyConfiguration(String defaultConf){
		super("", defaultConf);
	}
	
	public void initConfiguration(String filePath, Map<String, String> defaultConf){
		try{
			configuration = new HashMap<>();
			configuration.putAll(defaultConf);
			
			this.file = null;
			
		}catch(Exception e){
			ChardServer.getInstance().log(e, LogLevel.CRITICAL);
		}
	}
	
	public void save(){
		throw new UnsupportedOperationException();
	}
}
