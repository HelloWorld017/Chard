package org.khinenw.chard.utils;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;

public class EncryptionHelper {
	public static EncryptionData encryptRSA(String plain) throws Exception{
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(2048);
		  
		KeyPair keyPair = generator.genKeyPair();
		
		Key publicKey = keyPair.getPublic();
		Key privateKey = keyPair.getPrivate();
		
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		String cipherText = toHex(cipher.doFinal(plain.getBytes()));
		
		return new EncryptionData(publicKey, privateKey, cipherText);
	}
	
	public static String decrypt(String cipherText, Key privateKey) throws Exception{
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		return new String(cipher.doFinal(fromHex(cipherText)));
	}
	
	public static String hash(String plain) throws Exception{
        MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
        sha512.update(plain.getBytes());
        
        StringBuilder builder = new StringBuilder();
        for (byte b : sha512.digest()) builder.append(Integer.toHexString(0xff & b));
        
        return builder.toString();
	}
	
	public static PrivateKey loadPrivateKey(String key64) throws GeneralSecurityException {
	    byte[] clear = fromHex(key64);
	    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
	    KeyFactory fact = KeyFactory.getInstance("DSA");
	    PrivateKey priv = fact.generatePrivate(keySpec);
	    Arrays.fill(clear, (byte) 0);
	    return priv;
	}


	public static PublicKey loadPublicKey(String stored) throws GeneralSecurityException {
	    byte[] data = fromHex(stored);
	    X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
	    KeyFactory fact = KeyFactory.getInstance("DSA");
	    return fact.generatePublic(spec);
	}

	public static String savePrivateKey(PrivateKey priv) throws GeneralSecurityException {
	    KeyFactory fact = KeyFactory.getInstance("DSA");
	    PKCS8EncodedKeySpec spec = fact.getKeySpec(priv,
	            PKCS8EncodedKeySpec.class);
	    byte[] packed = spec.getEncoded();
	    String key64 = toHex(packed);

	    Arrays.fill(packed, (byte) 0);
	    return key64;
	}


	public static String savePublicKey(PublicKey publ) throws GeneralSecurityException {
	    KeyFactory fact = KeyFactory.getInstance("DSA");
	    X509EncodedKeySpec spec = fact.getKeySpec(publ,
	            X509EncodedKeySpec.class);
	    return toHex(spec.getEncoded());
	}
	
	public static String toHex(byte[] b){
		return new BigInteger(b).toString(16);
	}
	
	public static byte[] fromHex(String string){
		return new BigInteger(string, 16).toByteArray();
	}
}
