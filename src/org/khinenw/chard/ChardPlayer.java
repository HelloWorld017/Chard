package org.khinenw.chard;

import org.khinenw.chard.network.Session;
import org.khinenw.chard.network.packet.Packet;
import org.khinenw.chard.utils.Configuration;

public class ChardPlayer {
	private PlayerStatus status;
	private Configuration playerData;
	private Session session;
	private String id;
	
	public ChardPlayer(String id, Session s){
		this.status = PlayerStatus.NOT_AUTHENTICATED;
		this.id = id;
		this.playerData = new Configuration(id + ".plconf", "default.plconf");
		this.session = s;
	}
	
	public String getName(){
		return this.id;
	}
	
	public Session getSession(){
		return this.session;
	}
	
	public void sendPacket(Packet pk){
		this.session.sendPacket(pk);
	}
	
	public boolean authenticate(String pw){
		if(this.status != PlayerStatus.NOT_AUTHENTICATED) return false;
		
		if(playerData.get("pw") == pw){
			this.status = PlayerStatus.DEFAULT;
			return true;
		}
		return false;
	}
	
	public void kick(){
		
	}
	
	public void kick(String reason){
	
	}
	
	public enum PlayerStatus{
		UNREGISTERED, NOT_AUTHENTICATED, DEFAULT, IN_GAME
	}
}
