package org.khinenw.chard.network.packet;

import java.nio.ByteBuffer;

public abstract class SplitablePacket extends Packet{
	public boolean hasSplit = false;
	public short splitCount = 0;
	public short splitIndex = 0;
	
	public byte[] buffer;
	
	public void encode(){
		super.encode();
		payload = ByteBuffer.allocate(64 * 64 * 64);
		writeInt(this.getID());
		writeBoolean(hasSplit);
		if(hasSplit){
			writeShort(splitCount);
			writeShort(splitIndex);
		}
		
		writeRaw(buffer);
		_encode();
	}
	
	public void decode(){
		readInt();
		
		hasSplit = readBoolean();
		if(hasSplit){
			splitCount = readShort();
			splitIndex = readShort();
		}
		_decode();
	}
}