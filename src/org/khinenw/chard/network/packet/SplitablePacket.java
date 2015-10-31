package org.khinenw.chard.network.packet;

public abstract class SplitablePacket extends Packet{
	public boolean hasSplit = false;
	public short splitCount = 0;
	public short splitIndex = 0;
	public int splitId = 0;
	
	public byte[] buffer;
	
	public void encode(){
		super.encode();
		
		this.writeBoolean(hasSplit);
		this.writeInt(splitId);
		this.writeShort(splitCount);
		this.writeShort(splitIndex);
		this.writeRaw(buffer);
	}
	
	public void decode(){
		super.decode();
		
		hasSplit = readBoolean();
		splitId = readShort();
		splitCount = readShort();
		splitIndex = readShort();
	}
}
