package org.khinenw.chard.network.packet;

public class RegistrationRequestPacket extends Packet{

	@Override
	public int getID(){
		return PacketInfo.REGISTRATION_REQUEST.ID;
	}

	@Override
	public void _encode(){
		throw new UnsupportedOperationException();
	}

	@Override
	public void _decode(){
		
	}

}
