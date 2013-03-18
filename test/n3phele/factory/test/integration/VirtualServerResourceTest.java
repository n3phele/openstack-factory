package n3phele.factory.test.integration;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.ws.rs.core.UriBuilder;

import n3phele.security.EncryptedHPCredentials;
import n3phele.service.model.core.ExecutionFactoryCreateRequest;
import n3phele.service.model.core.NameValue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;

public class VirtualServerResourceTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}


	@Before
	public void setUp() throws Exception {
		client = Client.create();
		//FIXME this is not needed? bug?
		//client.addFilter(new HTTPBasicAuthFilter(Resource.get("factoryUser", ""), Resource.get("factorySecret", "")));
		
		//load all properties from the crendentials.properties file where sensible credentials are registered for tests
		try
		{
			testResource = new TestResource("n3phele.factory.test.integration.credentials");
		}
		catch(FileNotFoundException e)
		{			
			throw new FileNotFoundException("The necessary file with test credentials was not found. Manually create the file and put real credentials there so integration tests can reach the cloud. See tests for necessary variables.");
		}
		
		String serverAddress = testResource.get("testServerAddress", "http://127.0.0.1:8888");
		webResource = client.resource(UriBuilder.fromUri(serverAddress + "/resources/virtualServer").build());
	}

	private Client client;
	private WebResource webResource;
	private TestResource testResource;

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAccountTest() throws UnsupportedEncodingException, NoSuchAlgorithmException {
		WebResource resource =  webResource.path("/accountTest");
		
		Form form = new Form();
				
		String accessId = EncryptedHPCredentials.encryptX(testResource.get("testAccessId", ""), "password");
		String secret = EncryptedHPCredentials.encryptX(testResource.get("testAccessKey", ""), "password");

		form.add("fix", true);
		form.add("id", accessId);
		form.add("secret", secret);
		form.add("key", "mykey");
		form.add("location", "https://az-1.region-a.geo-1.ec2-compute.hpcloudsvc.com/services/Cloud");
		form.add("locationId", "az-1.region-a.geo-1");
		form.add("firstName", "User");
		form.add("lastName", "LastName");
		form.add("securityGroup", "default-lis");
		form.add("email", "test@cpca.pucrs.br");

		ClientResponse result = resource.post(ClientResponse.class, form);		
		
		assertEquals(200, result.getStatus());
	}
	
	@Test
	public void testCreateVM() throws UnsupportedEncodingException, NoSuchAlgorithmException, URISyntaxException {
		WebResource resource =  webResource.path("/");

		String accessId = EncryptedHPCredentials.encryptX(testResource.get("testAccessId", ""), "password");
		String secret = EncryptedHPCredentials.encryptX(testResource.get("testAccessKey", ""), "password");
		
		ExecutionFactoryCreateRequest request = new ExecutionFactoryCreateRequest();
		
		request.accessKey = accessId;
		request.encryptedSecret = secret;
		request.location = new URI("https://az-1.region-a.geo-1.ec2-compute.hpcloudsvc.com/services/Cloud");
		request.description = "description";
		request.name = "name";
		request.owner = new URI("http://localhost/");
		ArrayList<NameValue> parameters = new ArrayList<NameValue>();
		parameters.add(new NameValue("minCount", "1"));
		parameters.add(new NameValue("maxCount", "1"));
		parameters.add(new NameValue("imageId", "75845"));
		parameters.add(new NameValue("instanceType", "100"));
		parameters.add(new NameValue("securityGroup", "default"));
		parameters.add(new NameValue("keyName", "liskey"));
		parameters.add(new NameValue("locationId", "az-1.region-a.geo-1"));
		parameters.add(new NameValue("userData", ""));
		request.parameters = parameters;

		ClientResponse result = resource.post(ClientResponse.class, request);
	}

}
