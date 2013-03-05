package n3phele.factory.test.units;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.UriBuilder;

import n3phele.factory.model.ServiceModelDao;
import n3phele.factory.rest.impl.VirtualServerResource;
import n3phele.service.core.Resource;
import n3phele.service.model.core.AbstractManager;
import n3phele.service.model.core.BaseEntity;
import n3phele.service.model.core.Collection;
import n3phele.service.model.core.GenericModelDao;
import n3phele.service.model.core.NameValue;
import n3phele.service.model.core.VirtualServer;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.mockito.Mockito.*;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;

public class VirtualServerResourceTest {

	private final LocalServiceTestHelper helper =   new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig(),
			new LocalTaskQueueTestConfig()
								.setDisableAutoTaskExecution(false)             
								.setCallbackClass(LocalTaskQueueTestConfig.DeferredTaskCallback.class)) ;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		helper.setUp();
	}

	@After     
	public void tearDown() {         
		helper.tearDown();     
	} 


	@Test(expected = IllegalArgumentException.class)
	public void accountTestThrowExceptionWhenEmptyParametersWithFix() {
		VirtualServerResource resource = new VirtualServerResource();
		
		URI uri = null;
		try {
			uri = new URI("http://localhost/");
		} catch (URISyntaxException e) {
			fail();
		}
		
		resource.accountTest(true, "1", "", "", uri, "", "", "", "", "");
		fail("exception was expected");
	}
	
	@Test
	public void accountTestOnCheckedKeyAndGroup() {
		//Create test resources mocking calls to remote API
		VirtualServerResource resource = new VirtualServerResource() {
			@Override
			protected boolean checkSecurityGroup(String groupName, String id, String secret, URI location, String locationId)
			{
				return true;
			}
			
			@Override
			protected boolean checkKey(String key, String id, String secret, URI location, String locationId)
			{
				return true;
			}		
		};
		
		URI uri = null;
		try {
			uri = new URI("http://localhost/");
		} catch (URISyntaxException e) {
			fail();
		}
		
		String result = resource.accountTest(true, "1", "secret", "key", uri, "locationId", "email", "firstName", "lastName", "securityGroup");
		assertEquals("", result);		
	}
	
	@Test
	public void accountTestOnBadKeyAndGroup() {
		//Create test resources mocking calls to remote API
		
		VirtualServerResource resource = new VirtualServerResource() {
			
			@Override
			protected boolean checkSecurityGroup(String groupName, String id, String secret, URI location, String locationId)
			{
				return false;
			}
			
			@Override
			protected boolean checkKey(String key, String id, String secret, URI location, String locationId)
			{
				return false;
			}

			//FIXME how to verify if this function was really called?
			@Override
			protected boolean makeSecurityGroup(String groupName, String id, String secret, URI location, String to, String firstName, String lastName, String locationId)
			{
				return true;
			}
			
			@Override
			protected boolean createKey(String key, String id, String secret, URI location, String email, String firstName, String lastName, String locationId)
			{
				return true;
			}
			
		};
		
		URI uri = null;
		try {
			uri = new URI("http://localhost/");
		} catch (URISyntaxException e) {
			fail();
		}
		
		String result = resource.accountTest(true, "1", "secret", "key", uri, "locationId", "email", "firstName", "lastName", "securityGroup");
		assertEquals("", result);
	}
	
	@Test
	public void virtualServerListTest() {

		VirtualServerManager manager = new VirtualServerManager();
				
		//Add a virtual server object to database
		VirtualServer vs = createFakeDataVirtualServer();
		manager.add(vs);
		
		//Test list method return from resource
		VirtualServerResource resource = new VirtualServerResource();		
		Collection<BaseEntity> collection = resource.list(false);
				
		//Verify if returned the virtual server from database
		assertEquals(1, collection.getElements().size());		
	}
	
	@Test
	public void virtualServerGetTest() {

		VirtualServerManager manager = new VirtualServerManager();
				
		//Add a virtual server object to database
		VirtualServer vs = createFakeDataVirtualServer();
		long id = 1111l;
		vs.setId(id);
		manager.add(vs);
		
		//Test get method return from resource
		VirtualServerResource resource = new VirtualServerResource() {
			//Do nothing when trying to update reference throw remote call
			@Override
			protected void updateVirtualServer(VirtualServer item, UUID reference, int sequence) throws IllegalArgumentException
			{
				
			}			
		};		
		VirtualServer virtualServer = resource.get(id);
				
		//Verify if returned the virtual server from database
		assertEquals(vs.getId(), virtualServer.getId());	
		assertEquals(vs.getLocation(), virtualServer.getLocation());
		assertEquals(vs.getAccessKey(), virtualServer.getAccessKey());
		assertEquals(vs.getEncryptedKey(), virtualServer.getEncryptedKey());
		assertEquals(vs.getInstanceId(), virtualServer.getInstanceId());
	}
	
	@Test
	public void virtualServerKillTest() {

		VirtualServerManager manager = new VirtualServerManager();
				
		//Add a virtual server object to database
		VirtualServer vs = createFakeDataVirtualServer();
		long id = 1111l;
		vs.setId(id);
		manager.add(vs);
		
		//Test get method return from resource
		VirtualServerResource resource = new VirtualServerResource() {
			//Do nothing when trying to update reference throw remote call
			@Override
			protected void updateVirtualServer(VirtualServer item, UUID reference, int sequence) throws IllegalArgumentException
			{
				
			}
			
			@Override
			protected void terminate(VirtualServer virtualServer)
			{

			}
			
			//FIXME: How to verify if method was called?
			@Override
			protected void softKill(VirtualServer virtualServer, boolean error)
			{
				
			}
		};
		
		resource.kill(vs.getId(), false, false);		
	}

	private VirtualServer createFakeDataVirtualServer() {
		URI uri = null;
		try {
			uri = new URI("http://localhost/");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		VirtualServer vs = new VirtualServer("", "", uri, new ArrayList<NameValue>(), uri, "", "", "", "", uri, "");
		return vs;
	}
	

	public static class VirtualServerManager extends AbstractManager<VirtualServer> {

		@Override
		protected URI myPath()
		{
			return UriBuilder.fromUri(Resource.get("baseURI", "http://localhost:8889/resources")).path("virtualServer").build();
		}

		@Override
		protected GenericModelDao<VirtualServer> itemDaoFactory(boolean transactional)
		{
			return new ServiceModelDao<VirtualServer>(VirtualServer.class, transactional);
		}
	}

}