package org.khinenw.chard.network.packet;

public enum PacketInfo {
	PING(0x00), PONG(0x01), LOGIN(0x02);
	
	public final int ID;
	
	private PacketInfo(int ID){
		this.ID = ID;
	}
}
