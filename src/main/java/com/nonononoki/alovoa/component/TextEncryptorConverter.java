package com.nonononoki.alovoa.component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TextEncryptorConverter implements AttributeConverter<String, String> {
	
	@Value("${app.text.key}")
	private String key;
	
	@Value("${app.text.salt}")
	private String salt;
	
	private final String TRANSFORMATION = "AES/CBC/PKCS5PADDING";
	
	private static IvParameterSpec ivSpec;
	private static SecretKeySpec keySpec;
	private static Cipher enCipher;
	private static Cipher deCipher;
	
	private IvParameterSpec getIvSpec() throws Exception {
		if(ivSpec == null) {
			ivSpec = new IvParameterSpec(salt.getBytes("UTF-8"));
		}
		
		return ivSpec;
	}
	private SecretKeySpec getKeySpec() throws Exception {
		if(keySpec == null) {
			keySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
		} 
		return keySpec;
		
	}
	
	private Cipher getEnCipher() throws Exception {
		if(enCipher == null) {
			enCipher = Cipher.getInstance(TRANSFORMATION);
			enCipher.init(Cipher.ENCRYPT_MODE, getKeySpec(), getIvSpec());
		} 		
		return enCipher;		
	}
	
	private Cipher getDeCipher() throws Exception {
		if(deCipher == null) {
			deCipher = Cipher.getInstance(TRANSFORMATION);
			deCipher.init(Cipher.DECRYPT_MODE, getKeySpec(), getIvSpec());
		} 	
		return deCipher;	
	}

	
	@Override
	public String convertToDatabaseColumn(String attribute) {
		//encrypt
		try {
	        byte[] ba = getEnCipher().doFinal(attribute.getBytes());
	        return Base64.encodeBase64String(ba);
		} catch (Exception e) {
			//e.printStackTrace();
			return null;
		}
	}

	@Override
	public String convertToEntityAttribute(String dbData) {
		//decrypt		
		try {
	        byte[] ba = getDeCipher().doFinal(Base64.decodeBase64(dbData));
	        return new String(ba); 
		} catch (Exception e) {
			//e.printStackTrace();
			return null;
		}
	}
}