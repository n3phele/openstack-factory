/**
 * (C) Copyright 2010-2012. Nigel Cook. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 * Licensed under the terms described in LICENSE file that accompanied this code, (the "License"); you may not use this file
 * except in compliance with the License. 
 * 
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on 
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 *  specific language governing permissions and limitations under the License.
 */
 
 package n3phele.security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import com.sun.jersey.core.util.Base64;

/**
 * @author Nigel Cook
 * @author Alexandre Leites
 * @author Cristina Scheibler
 */
public class EncryptedHPCredentials implements EncryptedCredentials {
	protected static Logger log = Logger.getLogger(EncryptedHPCredentials.class.getName());
	
	private String accessKey;
	private String secretKey;
	private String password;
	
	public EncryptedHPCredentials(String passwd)
	{
		this.password = passwd;
	}
	
	/* (non-Javadoc)
	 * @see n3phele.security.CredentialsEncryptor#setCredentials(java.lang.String, java.lang.String)
	 */
	@Override
	public void setCredentials(String encryptedAccessKey, String encryptedSecretKey) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		this.accessKey = encryptedAccessKey;
		this.secretKey = encryptedSecretKey;
		decrypt(encryptedAccessKey, this.password);
		decrypt(encryptedSecretKey, this.password);
	}
	
	public static String decrypt(String encrypted, String passwd) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		
		byte[] key = (passwd).getBytes("UTF-8"); 
		MessageDigest sha = MessageDigest.getInstance("SHA-1"); 
		key = sha.digest(key); 
		key = Arrays.copyOf(key, 16); // use only first 128 bit 

		
		SecretKeySpec spec = new SecretKeySpec(key, "AES");
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, spec);
			return new String(cipher.doFinal(Base64.decode(encrypted)));
		} catch (InvalidKeyException e) {
			log.log(Level.SEVERE, "Decryption error", e);
			throw new IllegalArgumentException(e);
		} catch (NoSuchAlgorithmException e) {
			log.log(Level.SEVERE, "Decryption error", e);
			throw new IllegalArgumentException(e);
		} catch (NoSuchPaddingException e) {
			log.log(Level.SEVERE, "Decryption error", e);
			throw new IllegalArgumentException(e);
		} catch (IllegalBlockSizeException e) {
			log.log(Level.SEVERE, "Decryption error", e);
			throw new IllegalArgumentException(e);
		} catch (BadPaddingException e) {
			log.log(Level.SEVERE, "Decryption error", e);
			throw new IllegalArgumentException(e);
		}
		
	}
	
	public static String encryptX(String str, String passwd) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		byte[] key = (passwd).getBytes("UTF-8"); 
		MessageDigest sha = MessageDigest.getInstance("SHA-1"); 
		key = sha.digest(key); 
		key = Arrays.copyOf(key, 16); // use only first 128 bit 
		SecretKeySpec spec = new SecretKeySpec(key, "AES");
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, spec);
			return new String(Base64.encode(cipher.doFinal(str.getBytes("UTF-8"))));
		} catch (InvalidKeyException e) {
			log.log(Level.SEVERE, "Decryption error", e);
			throw new IllegalArgumentException(e);
		} catch (NoSuchAlgorithmException e) {
			log.log(Level.SEVERE, "Decryption error", e);
			throw new IllegalArgumentException(e);
		} catch (NoSuchPaddingException e) {
			log.log(Level.SEVERE, "Decryption error", e);
			throw new IllegalArgumentException(e);
		} catch (IllegalBlockSizeException e) {
			log.log(Level.SEVERE, "Decryption error", e);
			throw new IllegalArgumentException(e);
		} catch (BadPaddingException e) {
			log.log(Level.SEVERE, "Decryption error", e);
			throw new IllegalArgumentException(e);
		}
		
	}

	/* (non-Javadoc)
	 * @see n3phele.security.CredentialsEncryptor#getHPAccessKeyId()
	 */
	@Override
	public String getHPAccessKeyId() {
		try {
			return decrypt(this.accessKey, this.password);
		} catch (UnsupportedEncodingException e) {
			return null;
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see n3phele.security.CredentialsEncryptor#getHPSecretKey()
	 */
	@Override
	public String getHPSecretKey() {
		try {
			return decrypt(this.secretKey, this.password);
		} catch (UnsupportedEncodingException e) {
			return null;
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}
}
