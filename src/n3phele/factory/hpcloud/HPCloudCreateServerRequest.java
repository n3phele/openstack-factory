package n3phele.factory.hpcloud;

public class HPCloudCreateServerRequest {
	public String hardwareId;
	public String imageId;
	public String locationId;
	
	public String securityGroup;
	
	public int nodeCount;
	
	public HPCloudCreateServerRequest(String hardwareId, String imageId, String locationId, String securityGroup, int nodeCount)
	{
		this.hardwareId = hardwareId;
		this.imageId = imageId;
		this.locationId = locationId;
		this.securityGroup = securityGroup;
		this.nodeCount = nodeCount;
	}

	public HPCloudCreateServerRequest()
	{
		this("","","","",0);
	}
}
