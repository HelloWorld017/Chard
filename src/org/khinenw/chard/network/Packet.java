package org.khinenw.chard.network;

import java.nio.ByteBuffer;

public abstract class Packet{
	public byte[] buffer;
	public ByteBuffer payload;
	protected int offset = 0;
	
	public abstract String getName();
	
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
		boolean isSigned = readBoolean();
		byte[] bytes = readRaw(2);
		if(!isSigned){
			return (short) (((bytes[0] << 8) & 0x0000ff00) | (bytes[1] & 0x000000ff));
		}
		return ByteBuffer.wrap(readRaw(2)).getShort();
	}
	
	public void writeShort(short s, boolean isSigned){
		if(isSigned){
			
		}
	}
	
	public int readInt(){
		return ByteBuffer.wrap(readRaw(4)).getInt();
	}
	
	public void writeInt(int i){
		
	}
	
	public boolean readBoolean(){
		if(readByte() == 0) return true;
		return false;
	}
	
	public void writeBoolean(){
		
	}
	
	public String readString(){
		return new String(readRaw(readShort()));
	}
	
	public void writeString(String s){
		writeShort((short) s.getBytes().length, false);
        writeRaw(s.getBytes());
	}
	
	public abstract void _encode();
	public abstract void _decode();
	
	public void encode(){
		payload = ByteBuffer.allocate(64 * 64 * 64);
		_encode();
	}
	
	public void decode(){
		readString();
		_decode();
	}
}
