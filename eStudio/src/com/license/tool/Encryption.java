package com.license.tool;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {

	public static String md5Encrypt(String string) {
		if(string == null || string.trim().length() ==0 ){
			return "";
		}
		
		string = string.trim();
		String encString = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update( string.getBytes(),0,string.length() );
			encString = new BigInteger(1, md.digest()).toString(16);
			encString = encString.trim();
			if(encString.length() == 31){
				encString = "0"+encString;
			}
		}
		catch (NoSuchAlgorithmException e1) {
			   e1.printStackTrace();
		}

		return encString;
		
	}

	public static byte[] encryptMessage(String message, String strPK ){
		Global.LOG.info(" Encription encryptMessage() - START ");
		try {
			byte[] privatekey = strPK.getBytes("UTF-8"); 
		
		    byte[] byteArrayPlainText = message.getBytes("UTF-8");
			
		    SecretKeySpec secretKey = new SecretKeySpec(privatekey, "AES");
		    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		    
		    cipher.init( Cipher.ENCRYPT_MODE, secretKey);
		    byte[] byteArrayMessage = cipher.doFinal(byteArrayPlainText);
		    Global.LOG.info(" Encription encriptMessage() - END ");
		    return byteArrayMessage;
		}
		catch (Exception e1) {
			   e1.printStackTrace();
		}
		Global.LOG.info(" Encription encriptMessage() - END ");
	    return null;
	}

	public static  String decryptMessage( byte[] byteArrayCipherText, String strPK ){
		Global.LOG.info(" Encription decryptMessage() - START ");
		try {
		    byte[] privatekey = strPK.getBytes("UTF-8"); 
		
		    SecretKeySpec secretKey = new SecretKeySpec(privatekey, "AES");
		    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		    
		    cipher.init( Cipher.DECRYPT_MODE, secretKey);
		    byte[] byteArrayMessage = cipher.doFinal(byteArrayCipherText);
		    
		    Global.LOG.info(" Encription decryptMessage() - END ");    
		    return new String( byteArrayMessage, "UTF-8" );
		}
		catch (Exception e) {
			Global.LOG.info(" Problem to decript message ", e);
			e.printStackTrace();
		}
		Global.LOG.info(" Encription decryptMessage() - END ");
	    return null;
	}

	
}
