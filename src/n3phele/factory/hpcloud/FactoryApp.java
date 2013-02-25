package n3phele.factory.hpcloud;

import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.jclouds.compute.domain.Image;

@Path("/hpcloud")
public class FactoryApp {
	
	@Context
	UriInfo uriInfo;
	
	@Context
	SecurityContext securityContext;
	
	HPCloudManager	hpcManager;

	public FactoryApp(String identity, String secretKey)
	{
		HPCloudCredentials credentials = new HPCloudCredentials(identity, secretKey);
		hpcManager = new HPCloudManager(credentials);
	}

	public void run()
	{
		/*HPCloudCreateServerRequest nodeCreationReq = new HPCloudCreateServerRequest();

		nodeCreationReq.hardwareId = "az-1.region-a.geo-1/100";
		nodeCreationReq.imageId = "az-1.region-a.geo-1/75845";
		nodeCreationReq.locationId = "az-1.region-a.geo-1";
		nodeCreationReq.nodeCount = 1;
		nodeCreationReq.securityGroup = "lis-lis-nodes-1";

		List<HPCloudServer> nodes = hpcManager.createServerRequest(nodeCreationReq);
		for (HPCloudServer srv : nodes)
		{
			System.out.printf(">> Node -> ID: %s Name: %s \n", srv.getId(), srv.getName());
		}*/
	}
	
	@Path("/images")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Set<? extends Image> getImageList()
	{
		Set<? extends Image> imageList = hpcManager.listImages();
		
		return imageList;
	}
}
