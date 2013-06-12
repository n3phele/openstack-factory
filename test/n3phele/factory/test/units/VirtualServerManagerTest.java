package n3phele.factory.test.units;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.Assert;

import n3phele.factory.rest.impl.VirtualServerResource.VirtualServerManager;
import n3phele.service.model.core.Collection;
import n3phele.service.model.core.VirtualServer;
import n3phele.service.model.core.VirtualServerStatus;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;

public class VirtualServerManagerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	private final LocalServiceTestHelper	helper	= new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig(), new LocalTaskQueueTestConfig().setDisableAutoTaskExecution(false).setCallbackClass(LocalTaskQueueTestConfig.DeferredTaskCallback.class));

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		helper.setUp();
	}

	@After
	public void tearDown()
	{
		helper.tearDown();
	}

	@Test
	public void getNonTerminatedVMsFromDatabaseReturnTwoRunningVMsTest() throws URISyntaxException {
		VirtualServerManager manager = new VirtualServerManager();
		
		VirtualServer v1 = Utils.createFakeDataVirtualServer();
		v1.setId(1l);
		v1.setUri(new URI("http://localhost/1"));
		v1.setStatus(VirtualServerStatus.running);
		VirtualServer v2 = Utils.createFakeDataVirtualServer();
		v2.setStatus(VirtualServerStatus.running);
		v2.setId(2l);
		v2.setUri(new URI("http://localhost/2"));
		
		manager.add(v1);
		manager.add(v2);
		
		Collection<VirtualServer> servers = manager.getNotTerminatedMachines();
		
		Assert.assertEquals(2, servers.getElements().size());
	}
	
	@Test
	public void getNonTerminatedVMsFromDatabaseReturnTwoInitializingVMsTest() throws URISyntaxException {
		VirtualServerManager manager = new VirtualServerManager();
		
		VirtualServer v1 = Utils.createFakeDataVirtualServer();
		v1.setId(1l);
		v1.setUri(new URI("http://localhost/1"));
		v1.setStatus(VirtualServerStatus.initializing);
		VirtualServer v2 = Utils.createFakeDataVirtualServer();
		v2.setStatus(VirtualServerStatus.initializing);
		v2.setId(2l);
		v2.setUri(new URI("http://localhost/2"));
		
		manager.add(v1);
		manager.add(v2);
		
		Collection<VirtualServer> servers = manager.getNotTerminatedMachines();
		
		Assert.assertEquals(2, servers.getElements().size());
	}
	
	@Test
	public void getNonTerminatedVMsFromDatabaseReturnNoVMsTest() throws URISyntaxException {
		VirtualServerManager manager = new VirtualServerManager();
		
		VirtualServer v1 = Utils.createFakeDataVirtualServer();
		v1.setId(1l);
		v1.setUri(new URI("http://localhost/1"));
		v1.setStatus(VirtualServerStatus.terminated);
		VirtualServer v2 = Utils.createFakeDataVirtualServer();
		v2.setStatus(VirtualServerStatus.terminated);
		v2.setId(2l);
		v2.setUri(new URI("http://localhost/2"));
		
		manager.add(v1);
		manager.add(v2);
		
		Collection<VirtualServer> servers = manager.getNotTerminatedMachines();
		
		Assert.assertEquals(0, servers.getElements().size());
	}
	
	@Test
	public void getNonTerminatedVMsFromDatabaseReturnOneRunningAndOneInitilizingTest() throws URISyntaxException {
		VirtualServerManager manager = new VirtualServerManager();
		
		VirtualServer v1 = Utils.createFakeDataVirtualServer();
		v1.setId(1l);
		v1.setUri(new URI("http://localhost/1"));
		v1.setStatus(VirtualServerStatus.running);
		VirtualServer v2 = Utils.createFakeDataVirtualServer();
		v2.setStatus(VirtualServerStatus.initializing);
		v2.setId(2l);
		v2.setUri(new URI("http://localhost/2"));
		
		manager.add(v1);
		manager.add(v2);
		
		Collection<VirtualServer> servers = manager.getNotTerminatedMachines();
		
		Assert.assertEquals(2, servers.getElements().size());
	}

}
