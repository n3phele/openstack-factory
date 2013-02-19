package n3phele.factory.rest.impl;

import java.net.URI;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import n3phele.factory.hpcloud.FactoryApp;
import n3phele.factory.hpcloud.HPCloudCredentials;

@Path("/servers")
public class ServerResource {

	@Context
	UriInfo uriInfo;
	
	@Context
	SecurityContext securityContext;

	@GET
	@Produces("text/plain")
	public String getServers() {
		//FIXME: implement this correctly, for now is only a test
		return "Hello World! Test!";
	}

	@POST
	public Response createServer(HPCloudCredentials identity) {

		FactoryApp factory = new FactoryApp(identity.getIdentity(), identity.getSecretKey());

		try
		{
			factory.run();

			// Return a 201 code
			URI uri = null;
			//FIXME: should implement this correctly. This is a test.
			uri = new URI("http://YEY.com/");

			return Response.created(uri).build();
		}
		catch(Exception e)
		{
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

}
