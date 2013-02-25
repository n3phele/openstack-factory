package n3phele.factory.hpcloud;

import java.net.URI;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class HPCloudCreateServerResponse {
	public URI servers[];
	
	public HPCloudCreateServerResponse() { }
	
	public HPCloudCreateServerResponse(List<URI> serversList)
	{
		servers = serversList.toArray( new URI[serversList.size()] );
	}
}
