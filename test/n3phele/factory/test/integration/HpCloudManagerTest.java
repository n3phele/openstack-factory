package n3phele.factory.test.integration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import n3phele.factory.hpcloud.HPCloudCredentials;
import n3phele.factory.hpcloud.HPCloudManager;
import n3phele.security.EncryptedCredentials;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;

public class HpCloudManagerTest {

	private final LocalServiceTestHelper helper =   new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig(),
			new LocalTaskQueueTestConfig()
								.setDisableAutoTaskExecution(false)             
								.setCallbackClass(LocalTaskQueueTestConfig.DeferredTaskCallback.class)) ;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		helper.setUp();
		
		//load all properties from the crendentials.properties file where sensible credentials are registered for tests
		try
		{
			testResource = new TestResource("n3phele.factory.test.integration.credentials");
		}
		catch(FileNotFoundException e)
		{			
			throw new FileNotFoundException("The necessary file with test credentials was not found. Manually create the file and put real credentials there so integration tests can reach the cloud. See tests for necessary variables.");
		}
		
		EncryptedCredentials encriptor = mock(EncryptedCredentials.class);
		when(encriptor.getHPAccessKeyId()).thenReturn(testResource.get("testAccessId", ""));
		when(encriptor.getHPSecretKey()).thenReturn(testResource.get("testAccessKey", ""));
		
		HPCloudCredentials credentials = new HPCloudCredentials("", "", encriptor);
		hpCloudManager = new HPCloudManager(credentials);
	}
	
	HPCloudManager hpCloudManager;
	private TestResource testResource;

	@After
	public void tearDown() throws Exception { 
		helper.tearDown();    
	}

	@Test
	public void rebuildServerTest() throws UnsupportedEncodingException, NoSuchAlgorithmException {
		
		hpCloudManager.rebuildNode("az-1.region-a.geo-1", "1458089");
	}
}
	