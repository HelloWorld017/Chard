package org.khinenw.chard.network.packet;

public class LoginRequestPacket extends Packet{

	@Override
	public int getID(){
		return PacketInfo.LOGIN_REQUEST.ID;
	}

	@Override
	public void _encode(){
		throw new UnsupportedOperationException();
	}

	@Override
	public void _decode(){
		
	}

}
