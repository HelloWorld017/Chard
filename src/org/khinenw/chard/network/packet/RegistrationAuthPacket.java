package org.khinenw.chard.network.packet;

public class RegistrationAuthPacket extends Packet{

	public String playerId;
	public String authToken;
	
	@Override
	public int getID(){
		return PacketInfo.REGISTRATION_AUTH.ID;
	}

	@Override
	public void _encode(){
		throw new UnsupportedOperationException();
	}

	@Override
	public void _decode(){
		playerId = readString();
		authToken = readString();
	}

}
