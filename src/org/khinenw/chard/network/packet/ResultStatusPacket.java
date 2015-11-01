package org.khinenw.chard.network.packet;

public class ResultStatusPacket extends Packet{
	
	public int status;
	public String message = "";
	
	public static final int SUCCESS = 0x00;
	public static final int FAIL_SERVER_FAULT = 0x01;
	public static final int FAIL_WRONG_DATA = 0X02;
	public static final int FAIL_OUTDATED_CLIENT = 0x03;
	public static final int FAIL_OUTDATED_SERVER = 0x04;
	public static final int FAIL_UNKNOWN = 0X05;

	@Override
	public int getID(){
		return PacketInfo.RESULT_STATUS.ID;
	}

	@Override
	public void _encode(){
		writeInt(status);
		writeString(message);
	}

	@Override
	public void _decode(){
		status = readInt();
		message = readString();
	}
}
