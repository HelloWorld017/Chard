package org.khinenw.chard.event;

import org.khinenw.chard.network.packet.Packet;

public class PacketIncomeEvent extends CancellableEvent{
	private Packet pk;
	
	public PacketIncomeEvent(Packet pk){
		this.pk = pk;
	}
	@Override
	public String getEventName() {
		return "PacketIncome";
	}
	
	public Packet getPacket(){
		return pk;
	}
}
