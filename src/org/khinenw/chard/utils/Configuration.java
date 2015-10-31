package org.khinenw.chard.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.khinenw.chard.ChardServer;
import org.khinenw.chard.utils.Logger.LogLevel;

public class Configuration {
	private Map<String, String> configuration;
	private File file;
	
	public Configuration(String filePath, String defaultPath){
		try{
			Map<String, String> defaultConf = new HashMap<>();
			BufferedReader br = new BufferedReader(new FileReader(new File(defaultPath)));
			br.lines().forEach((v) -> {
				String[] array = v.split(":");
				defaultConf.put(array[0], String.join(":", Arrays.copyOfRange(array, 1, array.length)));
			});
			br.close();
			
			initConfiguration(filePath, defaultConf);
		}catch(Exception e){
			ChardServer.getInstance().log(e, LogLevel.CRITICAL);
		}
	}
	
	public Configuration(String filePath, Map<String, String> defaultConf){
		initConfiguration(filePath, defaultConf);
	}
	
	private void initConfiguration(String filePath, Map<String, String> defaultConf){
		try{
			configuration = new HashMap<>();
			configuration.putAll(defaultConf);
			
			this.file = new File(filePath);
			if(!file.exists()){
				file.createNewFile();
				return;
			}
			
			BufferedReader br = new BufferedReader(new FileReader(file));
			br.lines().forEach((v) -> {
				String[] array = v.split(":");
				configuration.put(array[0], String.join(":", Arrays.copyOfRange(array, 1, array.length)));
			});
			br.close();
			
		}catch(Exception e){
			ChardServer.getInstance().log(e, LogLevel.CRITICAL);
		}
	}
	
	public String get(String key){
		return configuration.get(key);
	}
	
	public boolean getBoolean(String key){
		return Boolean.valueOf(configuration.get(key));
	}
	
	public int getInt(String key){
		return Integer.parseInt(configuration.get(key));
	}
	
	public void set(String key, String value){
		this.configuration.put(key, value);
	}
	
	public void setAll(Map<String, String> configuration){
		this.configuration.putAll(configuration);
	}
	
	public void save(){
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			configuration.forEach((k, v) -> {
				try{
					bw.append(k + ":" + v);
				}catch(Exception e){
					ChardServer.getInstance().log(e, LogLevel.CRITICAL);
				}
			});
			bw.flush();
			bw.close();
		}catch(Exception e){
			ChardServer.getInstance().log(e, LogLevel.CRITICAL);
		}
	}
}
