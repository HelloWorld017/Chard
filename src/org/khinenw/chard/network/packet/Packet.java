package org.khinenw.chard.network.packet;

import java.nio.ByteBuffer;

import org.khinenw.chard.ChardServer;

public abstract class Packet implements Cloneable{
	public byte[] buffer;
	public ByteBuffer payload;
	protected int offset = 0;
	
	public abstract int getID();
	
	public byte[] readRaw(int len){
		ByteBuffer bb = ByteBuffer.wrap(buffer);
        bb.position(offset);
        byte[] bytes = new byte[len];
        bb.get(bytes);
        
        offset += bytes.length;
        return bytes;
	}
	
	public void writeRaw(byte[] b){
		payload.put(b);
	}
	
	public byte readByte(){
		byte b = buffer[offset];
		offset++;
		return b;
	}
	
	public void writeByte(byte b){
		payload.put(b);
	}
	
	public short readShort(){
		return ByteBuffer.wrap(readRaw(2)).getShort();
	}
	
	public void writeShort(short s){
		payload.put(ByteBuffer.allocate(2).putShort(s));
	}
	
	public int readInt(){
		return ByteBuffer.wrap(readRaw(4)).getInt();
	}
	
	public void writeInt(int i){
		payload.put(ByteBuffer.allocate(4).putInt(i));
	}
	
	public long readLong(){
		return ByteBuffer.wrap(readRaw(8)).getLong();
	}
	
	public void writeLong(long l){
		payload.put(ByteBuffer.allocate(8).putLong(l));
	}
	
	public boolean readBoolean(){
		if(readByte() == 0) return false;
		return true;
	}
	
	public void writeBoolean(boolean b){
		if(b){
			writeByte((byte) 0x01);
		}else{
			writeByte((byte) 0x00);
		}
	}
	
	public String readString(){
		return new String(readRaw(readShort()));
	}
	
	public void writeString(String s){
		writeShort((short) s.getBytes().length);
        writeRaw(s.getBytes());
	}
	
	public void encode(){
		payload = ByteBuffer.allocate(64 * 64 * 64);
		writeInt(this.getID());
		_encode();
	}
	
	public void decode(){
		readInt();
		_decode();
	}
	
	public abstract void _encode();
	public abstract void _decode();
	
	public Packet clone(){
		Packet newPk = ChardServer.getNetwork().getPacket(this.getID());
		this.encode();
		newPk.buffer = this.payload.array();
		newPk.decode();
		return newPk;
		
	}
}
