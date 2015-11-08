package org.khinenw.chard.network.packet;

public class RegistrationPacket extends Packet{

	public String id;
	public String pw;
	public String email;
	
	@Override
	public int getID(){
		return PacketInfo.REGISTRATION.ID;
	}

	@Override
	public void _encode(){
		throw new UnsupportedOperationException();
	}

	@Override
	public void _decode(){
		this.id = readString();
		this.pw = readString();
		this.email = readString();
	}

}
