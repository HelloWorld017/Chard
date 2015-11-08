package org.khinenw.chard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.khinenw.chard.network.Session;
import org.khinenw.chard.network.packet.Packet;
import org.khinenw.chard.utils.Configuration;
import org.khinenw.chard.utils.Logger.LogLevel;

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
	
	public void sendMail(String subject, File html, Configuration mailContents){
		StringBuilder htmlBuilder = new StringBuilder();
		try{
			BufferedReader br = new BufferedReader(new FileReader(html));
			br.lines().forEach(v -> htmlBuilder.append(v));
			br.close();
		}catch(Exception e){
			ChardServer.getInstance().log(e, LogLevel.WARNING);
		}
		
		String htmlContent = htmlBuilder.toString();
		
		mailContents.getAll().forEach((k, v) -> {
			htmlContent.replace("{" + k + "}", v);
		});
		
		Properties emailProperties = System.getProperties();
        emailProperties.put("mail.smtp.port", "586");
        emailProperties.put("mail.smtp.auth", "true");
        emailProperties.put("mail.smtp.starttls.enable", "true");
        javax.mail.Session mailSession = javax.mail.Session.getDefaultInstance(emailProperties, null);
        
		Message msg = new MimeMessage(mailSession);
		try{
			msg.setSubject(subject);
			msg.setRecipient(Message.RecipientType.TO, new InternetAddress(playerData.get("email"), false));
			msg.setContent(htmlContent, "text/html; charset=utf-8");
			msg.setSentDate(new Date());
			Transport.send(msg);
		}catch(MessagingException e){
			ChardServer.getInstance().log(e, LogLevel.WARNING);
		}
	}
	
	public void kick(){
		kick("kick");
	}
	
	public void kick(String reason){
	
	}
	
	public enum PlayerStatus{
		UNREGISTERED, NOT_AUTHENTICATED, DEFAULT, IN_GAME
	}
}
