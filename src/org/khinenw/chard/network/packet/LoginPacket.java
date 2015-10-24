package org.khinenw.chard.network.packet;

import org.khinenw.chard.network.Packet;

public class LoginPacket extends Packet{

	public String name;
	public String pw;

	@Override
	public String getName(){
		return "LOGIN";
	}
	
	@Override
	public void _encode() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void _decode(){
		String[] packetContent = (new String(buffer)).split("\n");
		this.name = packetContent[0];
		this.pw = packetContent[1];
	}

}
