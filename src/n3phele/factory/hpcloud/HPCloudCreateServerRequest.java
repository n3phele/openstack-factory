/**
 * @author Alexandre Leites
 */
package n3phele.factory.hpcloud;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class HPCloudCreateServerRequest {
	public String	flavorRef;
	public String	imageRef;
	public String	locationId;
	public String	keyName;
	public String	serverName;
	public String	user_data;

	public String	security_groups;

	public int		nodeCount;

	public HPCloudCreateServerRequest(String serverName, String flavorRef, String imageRef, String locationId, String security_groups, String keyPair, int nodeCount)
	{
		this.serverName = serverName;
		this.flavorRef = flavorRef;
		this.imageRef = imageRef;
		this.locationId = locationId;
		this.security_groups = security_groups;
		this.keyName = keyPair;
		this.nodeCount = nodeCount;
	}

	public HPCloudCreateServerRequest()
	{
		this("", "", "", "", "", "", 0);
	}
}
