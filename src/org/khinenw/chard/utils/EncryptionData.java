package org.khinenw.chard.utils;

import java.security.Key;

public class EncryptionData{
	private Key publicKey;
	private Key privateKey;
	private String cipherText;
	
	public EncryptionData(Key publicKey, Key privateKey, String cipherText){
		this.publicKey = publicKey;
		this.privateKey = privateKey;
		this.cipherText = cipherText;
	}
	
	public String getResult(){
		return cipherText;
	}
	
	public Key getPublicKey(){
		return publicKey;
	}
	
	public Key getPrivateKey(){
		return privateKey;
	}
}
