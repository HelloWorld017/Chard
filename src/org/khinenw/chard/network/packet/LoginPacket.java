package org.khinenw.chard.network.packet;

public class LoginPacket extends SplitablePacket{

	public String name;
	public String pw;

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
		String[] packetContent = (new String(buffer)).split("\n");
		this.name = packetContent[0];
		this.pw = packetContent[1];
	}
}
