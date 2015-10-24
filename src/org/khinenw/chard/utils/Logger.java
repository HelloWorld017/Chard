package org.khinenw.chard.utils;

public class Logger {
	public enum LogLevel{
		EMERGENCY(4), CRITICAL(3), WARNING(2), INFO(1), DEBUG(0);
		
		private int level;
		
		private LogLevel(int level){
			this.level = level;
		}
		
		public int getLevel(){
			return level;
		}
	}
}
