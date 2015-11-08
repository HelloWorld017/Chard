package org.khinenw.chard.network.packet;

public enum PacketInfo {
	//0x : General Packets
	PING(0x00), PONG(0x01), RESULT_STATUS(0x02),
	
	//1x : Account-associated Packets
	LOGIN_REQUEST(0x10), LOGIN_REPLY(0x11), LOGIN(0x12), LOGOUT(0x13), REGISTRATION_REQUEST(0x14), REGISTRATION_REPLY(0x14), REGISTRATION(0x15), REGISTRATION_AUTH(0x16);
	
	
	public final int ID;
	
	private PacketInfo(int ID){
		this.ID = ID;
	}
}
