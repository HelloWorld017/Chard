package org.khinenw.chard.network.packet;

import java.security.PublicKey;

public class RegistrationReplyPacket extends Packet{

	public PublicKey publicKey;
	@Override
	public int getID(){
		return PacketInfo.REGISTRATION_REPLY.ID;
	}

	@Override
	public void _encode(){
		writePublicKey(publicKey);
	}

	@Override
	public void _decode(){
		throw new UnsupportedOperationException();
	}

}
