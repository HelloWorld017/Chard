package org.khinenw.chard.event;

public interface Cancellable {
	public void setCancelled();
	public void setCancelled(boolean isCancelled);
	public boolean isCancelled();
}
