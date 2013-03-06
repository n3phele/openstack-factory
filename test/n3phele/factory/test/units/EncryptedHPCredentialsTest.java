package n3phele.factory.test.units;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import n3phele.security.EncryptedHPCredentials;

import org.junit.Before;
import org.junit.Test;

public class EncryptedHPCredentialsTest {
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		
	}
	
	@Test
	public void encryptXTest() throws UnsupportedEncodingException, NoSuchAlgorithmException
	{
		String result = EncryptedHPCredentials.encryptX("Tom123456789", "Jerry1");
		
		assertEquals("d34nDRLp+Qhky99AJXH+Ww==", result);
	}
	
	@Test
	public void decryptTest() throws UnsupportedEncodingException, NoSuchAlgorithmException
	{
		String result = EncryptedHPCredentials.decrypt("d34nDRLp+Qhky99AJXH+Ww==", "Jerry1");
		
		assertEquals("Tom123456789", result);
	}
	
	@Test
	public void decryptAccessKeyTest() throws UnsupportedEncodingException, NoSuchAlgorithmException
	{
		EncryptedHPCredentials creds = new EncryptedHPCredentials("aSRDm0DF+Xr/ABZXGQEF0Q==", "HxRKCsyKhzjNOuu9hDAtwQ==");
		
		System.out.println(creds.getHPAccessKeyId());
	}
	
	@Test
	public void decryptSecretKeyTest() throws UnsupportedEncodingException, NoSuchAlgorithmException
	{
		EncryptedHPCredentials creds = new EncryptedHPCredentials("aSRDm0DF+Xr/ABZXGQEF0Q==", "HxRKCsyKhzjNOuu9hDAtwQ==");
		
		System.out.println(creds.getHPSecretKey());
	}
}
