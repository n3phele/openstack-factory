/**
 * @author Alexandre Leites
 */
package n3phele.factory.hpcloud;

import java.net.URI;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class HPCloudCreateServerResponse {
	public URI servers[];
	public String instanceType;
	public String imageId;
	public String keyName;
	public String location;
	public String locationId;
	public String instanceId;
	
	public HPCloudCreateServerResponse() { }
	
	public HPCloudCreateServerResponse(List<URI> serversList, String instanceType, String imageId, String keyName, String location, String locationId, String instanceId)
	{
		servers = serversList.toArray( new URI[serversList.size()] );
		this.instanceType = instanceType;
		this.imageId = imageId;
		this.keyName = keyName;
		this.location = location;
		this.locationId = locationId;
		this.instanceId = instanceId;
		
	}
}
