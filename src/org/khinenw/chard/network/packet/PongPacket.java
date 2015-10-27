package org.khinenw.chard.network.packet;

public class PongPacket extends Packet{

	public short pingId;
	public long sendTime;
	
	@Override
	public int getID() {
		return PacketInfo.PONG.getID();
	}
	
	@Override
	public void encode() {
		super.encode();
		
		pingId = readShort();
		sendTime = readLong();
	}

	@Override
	public void decode() {
		throw new UnsupportedOperationException();
	}
}
