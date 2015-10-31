package org.khinenw.chard.network.packet;

public class PongPacket extends Packet{

	public short pingId;
	public long sendTime;
	
	@Override
	public int getID() {
		return PacketInfo.PONG.ID;
	}
	
	@Override
	public void _encode() {
		writeShort(pingId);
		writeLong(sendTime);
	}

	@Override
	public void _decode() {
		throw new UnsupportedOperationException();
	}
}
