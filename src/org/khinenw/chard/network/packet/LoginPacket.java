package org.khinenw.chard.network.packet;

public class LoginPacket extends Packet{

	public String name;
	public String pw;

	@Override
	public int getID(){
		return PacketInfo.LOGIN.getID();
	}
	
	@Override
	public void encode() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void decode(){
		super.decode();
		
		String[] packetContent = (new String(buffer)).split("\n");
		this.name = packetContent[0];
		this.pw = packetContent[1];
	}

}
