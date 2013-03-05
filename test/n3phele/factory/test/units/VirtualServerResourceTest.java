package n3phele.factory.test.units;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;

import n3phele.factory.rest.impl.VirtualServerResource;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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

}
