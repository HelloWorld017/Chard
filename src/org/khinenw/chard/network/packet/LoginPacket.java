package org.khinenw.chard.network.packet;

public class LoginPacket extends Packet{

	public String name;
	public String pw;
	public String version;

	@Override
	public int getID(){
		return PacketInfo.LOGIN.ID;
	}
	
	@Override
	public void _encode(){
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void _decode(){
		this.name = readString();
		this.pw = readString();
		this.version = readString();
	}
}
