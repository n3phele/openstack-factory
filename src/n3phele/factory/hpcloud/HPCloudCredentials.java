package n3phele.factory.hpcloud;

import javax.xml.bind.annotation.XmlRootElement;

import n3phele.security.EncryptedCredentials;
import n3phele.security.EncryptedHPCredentials;
import n3phele.service.core.Resource;

/**
 * @author Alexandre Leites
 */
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
		EncryptedCredentials creds = new EncryptedHPCredentials( Resource.get("factorySecret", "") );
		creds.setCredentials(identity, secretKey);
		
		this.identity = creds.getHPAccessKeyId();
		this.secretKey = creds.getHPSecretKey();
	}
	
	/**
	 * @param identity A join of TenantName and AccessKey with a ":" between them.
	 * @param secretKey secretKey associated with AccessKey provided in identity parameter.
	 */
	public HPCloudCredentials(String identity, String secretKey, EncryptedCredentials encryptionManager) throws Exception
	{
		EncryptedCredentials creds = encryptionManager;
		creds.setCredentials(identity, secretKey);
		
		this.identity = creds.getHPAccessKeyId();
		this.secretKey = creds.getHPSecretKey();
	}
	
	public HPCloudCredentials()
	{
		
	}
	
}
