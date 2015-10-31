package org.khinenw.chard.network.packet;

public class PingPacket extends Packet{

	public short pingId;
	public long sendTime;
	
	@Override
	public int getID() {
		return PacketInfo.PING.ID;
	}

	@Override
	public void _encode() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void _decode() {
		pingId = readShort();
		sendTime = readLong();
	}
}
