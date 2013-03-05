/**
 * @author Alexandre Leites
 */
package n3phele.factory.hpcloud;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.annotation.XmlRootElement;

import n3phele.security.EncryptedHPCredentials;


@XmlRootElement
public class HPCloudCredentials {
	/**
	 * Identity is a join of TenantName and AccessKey with a ":" between them.
	 */
	private String identity;
	/**
	 * Store the secretKey associated with AccessKey provided in identity parameter.
	 */
	private String secretKey;
	
	public String getSecretKey() {
		return secretKey;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	/**
	 * @param identity A join of TenantName and AccessKey with a ":" between them.
	 * @param secretKey secretKey associated with AccessKey provided in identity parameter.
	 */
	public HPCloudCredentials(String identity, String secretKey) throws Exception
	{
		EncryptedHPCredentials creds = new EncryptedHPCredentials(identity, secretKey);
		this.identity = creds.getHPAccessKeyId();
		this.secretKey = creds.getHPSecretKey();
	}
	
	public HPCloudCredentials()
	{
		
	}
	
}
