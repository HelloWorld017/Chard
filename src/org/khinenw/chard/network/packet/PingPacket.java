package org.khinenw.chard.network.packet;

public class PingPacket extends Packet{

	public short pingId;
	public long sendTime;
	
	@Override
	public int getID() {
		return PacketInfo.PING.getID();
	}

	@Override
	public void encode() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void decode() {
		super.decode();
		
		pingId = readShort();
		sendTime = readLong();
	}
}
