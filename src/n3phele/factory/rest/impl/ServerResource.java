package n3phele.factory.rest.impl;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

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
	public String getTest() {

		return "Hello World! Test!";
	}

	@POST
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createServer(HPCloudCredentials identity) {

		FactoryApp factory = new FactoryApp(identity.getIdentity(), identity.getSecretKey());
		factory.run();

		// Return a 201 code
		URI uri = null;
		try {
			uri = new URI("http://YEY.com/");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.created(uri).build();
	}

}
