package n3phele.factory.test.units;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import n3phele.factory.hpcloud.HPCloudManager;
import n3phele.factory.rest.impl.VirtualServerResource;
import n3phele.factory.rest.impl.ZombieStrategy;
import n3phele.service.core.Resource;
import n3phele.service.model.core.VirtualServer;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.mockito.Mockito.*;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;

public class ZombieStrategiesTest {

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

	@Test
	public void CreateVirtualMachineWithZombieNameTest() throws Exception {
		ZombieStrategy strategy = new ZombieStrategy();		
		VirtualServerResource resource = mock(VirtualServerResource.class);
		HPCloudManager hpCloudManager = mock(HPCloudManager.class);
		
		//Create a VM on database
		VirtualServerTestDAO dao = new VirtualServerTestDAO();		
		VirtualServer virtualServer = Utils.createFakeDataVirtualServer();
		virtualServer.setName("original");
		dao.add(virtualServer);		
		
		strategy.makeZombie(virtualServer, resource, hpCloudManager);

		VirtualServer v = Utils.createFakeDataVirtualServer();
		v.setName("zombie");
		
		//Verify if exist a zombie VM on database
		verify(resource).add(v);		
	}
	
	@Test
	public void CreateZombieSetVirtualMachineTagsAsZombieTest() throws Exception {
		ZombieStrategy strategy = new ZombieStrategy();		
		VirtualServerResource resource = mock(VirtualServerResource.class);
		HPCloudManager hpCloudManager = mock(HPCloudManager.class);
		
		//Create a VM on database
		VirtualServerTestDAO dao = new VirtualServerTestDAO();		
		VirtualServer virtualServer = Utils.createFakeDataVirtualServer();
		virtualServer.setInstanceId("12123");
		dao.add(virtualServer);
		
		when(resource.getLocationId(any(VirtualServer.class))).thenReturn("moon");
		
		strategy.makeZombie(virtualServer, resource, hpCloudManager);
		
		//Expected hash
		Map<String, String> tags = new HashMap<String, String>();
		tags.put("n3phele-name", virtualServer.getName());
		tags.put("n3phele-behavior", "zombie");
		tags.put("n3phele-factory", resource.FACTORY_NAME);
		tags.put("n3phele-uri", "");
		
		//Verify if exist a zombie VM on database
		verify(hpCloudManager).putServerTags("12123","moon" , tags);		
	}

}
