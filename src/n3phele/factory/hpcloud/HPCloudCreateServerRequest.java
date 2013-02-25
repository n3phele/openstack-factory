package n3phele.factory.hpcloud;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class HPCloudCreateServerRequest {
	public String	hardwareId;
	public String	imageId;
	public String	locationId;
	public String	keyPair;
	public String	serverName;
	public String	userData;

	public String	securityGroup;

	public int		nodeCount;

	public HPCloudCreateServerRequest(String serverName, String hardwareId, String imageId, String locationId, String securityGroup, String keyPair, int nodeCount)
	{
		this.serverName = serverName;
		this.hardwareId = hardwareId;
		this.imageId = imageId;
		this.locationId = locationId;
		this.securityGroup = securityGroup;
		this.keyPair = keyPair;
		this.nodeCount = nodeCount;
	}

	public HPCloudCreateServerRequest()
	{
		this("", "", "", "", "", "", 0);
	}
}
