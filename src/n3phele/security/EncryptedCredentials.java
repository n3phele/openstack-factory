package n3phele.security;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public interface EncryptedCredentials {

	public abstract void setCredentials(String encryptedAccessKey,
			String encryptedSecretKey) throws UnsupportedEncodingException,
			NoSuchAlgorithmException;

	public abstract String getHPAccessKeyId();

	public abstract String getHPSecretKey();

}