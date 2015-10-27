package org.khinenw.chard.event;

public class PlayerLoginEvent extends CancellableEvent{
	@Override
	public String getEventName() {
		return "PlayerLogin";
	}
}
