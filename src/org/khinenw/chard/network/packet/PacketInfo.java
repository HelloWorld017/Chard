package org.khinenw.chard.network.packet;

public enum PacketInfo {
	PING(0x00), PONG(0x01), LOGIN(0x02);
	
	private int ID;
	
	private PacketInfo(int ID){
		this.ID = ID;
	}
	
	public int getID(){
		return this.ID;
	}
}
