package org.khinenw.chard.network.packet;

public class LogoutPacket extends Packet{

	public String reason;
	
	@Override
	public int getID(){
		return PacketInfo.LOGOUT.ID;
	}

	@Override
	public void _encode(){
		throw new UnsupportedOperationException();
	}

	@Override
	public void _decode(){
		reason = readString();
	}

}
