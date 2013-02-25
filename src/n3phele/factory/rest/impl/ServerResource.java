package n3phele.factory.rest.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import n3phele.factory.hpcloud.HPCloudCreateServerRequest;
import n3phele.factory.hpcloud.HPCloudCreateServerResponse;
import n3phele.factory.hpcloud.HPCloudCredentials;
import n3phele.factory.hpcloud.HPCloudManager;
import n3phele.factory.hpcloud.HPCloudServer;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.domain.Location;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.ServerCreated;
import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.openstack.v2_0.domain.Link.Relation;

@Path("/")
public class ServerResource {
	@Context
	UriInfo					uriInfo;

	@Context
	SecurityContext			securityContext;

	private HPCloudManager	hpcManager;

	public ServerResource()
	{
		HPCloudCredentials credentials = new HPCloudCredentials("identity","secretKey");
		hpcManager = new HPCloudManager(credentials);
	}
	
	/**
	 * List all virtual machines
	 * @return List of virtual machines
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("servers")
	public ArrayList<Server> listServers()
	{
		ArrayList<Server> serversList = new ArrayList<Server>();
		
		return serversList;
	}
	
	/**
	 * Create one or more virtual machines based on our request
	 * @param r Request class
	 * @return State of current requests
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("servers")
	public Response createServer(HPCloudCreateServerRequest r)
	{
		if( r.hardwareId == null || r.imageId == null || r.locationId == null || r.serverName == null || r.securityGroup == null || r.keyPair == null || r.nodeCount < 1 )
		{
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		
		List<ServerCreated> serversList = hpcManager.createServerRequest(r);
		if(r != null)
		{
			List<URI> refList = new ArrayList<URI>(serversList.size());
			
			for(ServerCreated srv : serversList)
			{
				Set<Link> links = srv.getLinks();
				for(Link link : links)
				{
					if(link.getRelation().equals(Relation.SELF))
					{
						refList.add(link.getHref());
					}
				}
			}
			
			return Response.created(refList.get(0)).entity( new HPCloudCreateServerResponse(refList) ).build();
		}
		
		return Response.status(Response.Status.NO_CONTENT).build();
	}
	
	/**
	 * Get details about one specify server
	 * @param zone Compute zone
	 * @param Id Server Id
	 * @return Server class
	 */
	@GET
	@Produces({MediaType.APPLICATION_JSON,"application/vnd.com.n3phele.HPCloudServer+json" })
	@Path("servers/{zone}/{id}")
	public HPCloudServer getServerById(@PathParam("zone") String zone, @PathParam("id") String Id)
	{
		Server server = hpcManager.getServerById(zone, Id);
		HPCloudServer srv = new HPCloudServer();
		
		return srv;
	}
	
	/**
	 * Delete our server
	 * @param zone Compute zone
	 * @param Id Server Id
	 */
	@DELETE
	@Path("servers/{zone}/{id}")
	public void deleteServerById(@PathParam("zone") String zone, @PathParam("id") String Id)
	{
		hpcManager.terminateNode(zone, Id);
	}
}
