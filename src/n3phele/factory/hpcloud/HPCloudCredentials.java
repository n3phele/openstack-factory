package n3phele.factory.hpcloud;

public class HPCloudCredentials {
	/**
	 * Identity is a join of TenantName and AccessKey with a ":" between them.
	 */
	public String identity;
	/**
	 * Store the secretKey associated with AccessKey provided in identity parameter.
	 */
	public String secretKey;
	
	/**
	 * @param identity A join of TenantName and AccessKey with a ":" between them.
	 * @param secretKey secretKey associated with AccessKey provided in identity parameter.
	 */
	public HPCloudCredentials(String identity, String secretKey)
	{
		this.identity = identity;
		this.secretKey = secretKey;
	}
}
