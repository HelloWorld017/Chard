package org.khinenw.chard.event;

public abstract class CancellableEvent extends Event implements Cancellable{
	private boolean isCancelled = false;
	
	public void setCancelled(){
		setCancelled(true);
	}
	
	public void setCancelled(boolean isCancelled){
		this.isCancelled = isCancelled;
	}
	
	public boolean isCancelled(){
		return isCancelled;
	}
}
