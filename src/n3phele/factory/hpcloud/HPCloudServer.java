package n3phele.factory.hpcloud;

public class HPCloudServer {

	private String	id;
	private String	name;
	private String	imageId;
	private String	providerId;
	private String	hardwareId;
	private String	locationId;
	private String	status;
	private String	privateKey;

	public HPCloudServer(String id, String name, String imageId, String providerId, String hardwareId, String locationId, String status, String privateKey)
	{
		this.id = id;
		this.name = name;
		this.imageId = imageId;
		this.providerId = providerId;
		this.hardwareId = hardwareId;
		this.locationId = locationId;
		this.status = status;
	}

	/**
	 * @return the id
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return the imageId
	 */
	public String getImageId()
	{
		return imageId;
	}

	/**
	 * @return the providerId
	 */
	public String getProviderId()
	{
		return providerId;
	}

	/**
	 * @return the hardwareId
	 */
	public String getHardwareId()
	{
		return hardwareId;
	}

	/**
	 * @return the locationId
	 */
	public String getLocationId()
	{
		return locationId;
	}

	/**
	 * @return the status
	 */
	public String getStatus()
	{
		return status;
	}
	
	/**
	 * @return the privateKey
	 */
	public String getPrivateKey()
	{
		return privateKey;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @param imageId
	 *            the imageId to set
	 */
	public void setImageId(String imageId)
	{
		this.imageId = imageId;
	}

	/**
	 * @param providerId
	 *            the providerId to set
	 */
	public void setProviderId(String providerId)
	{
		this.providerId = providerId;
	}

	/**
	 * @param hardwareId
	 *            the hardwareId to set
	 */
	public void setHardwareId(String hardwareId)
	{
		this.hardwareId = hardwareId;
	}

	/**
	 * @param locationId
	 *            the locationId to set
	 */
	public void setLocationId(String locationId)
	{
		this.locationId = locationId;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status)
	{
		this.status = status;
	}

	/**
	 * @param privateKey the privateKey to set
	 */
	public void setPrivateKey(String privateKey)
	{
		this.privateKey = privateKey;
	}

	
}
