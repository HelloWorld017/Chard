package org.khinenw.chard.network.packet;

public enum PacketInfo {
	PING(0x00), PONG(0x01), RESULT_STATUS(0x02), LOGIN_REQUEST(0x03), LOGIN_REPLY(0x04), LOGIN(0x05);
	
	public final int ID;
	
	private PacketInfo(int ID){
		this.ID = ID;
	}
}
