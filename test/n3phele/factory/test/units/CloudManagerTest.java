package n3phele.factory.test.units;

import n3phele.factory.hpcloud.HPCloudCreateServerRequest;
import n3phele.factory.hpcloud.HPCloudCredentials;
import n3phele.factory.hpcloud.HPCloudManager;
import n3phele.factory.hpcloud.ServerOptionsFactory;

import org.jclouds.compute.ComputeService;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.nova.v2_0.options.CreateServerOptions;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;

import static org.mockito.Mockito.*;

public class CloudManagerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void callOneServerCreationTest() {
		HPCloudCredentials credentials = mock(HPCloudCredentials.class);
		final ComputeService service = mock(ComputeService.class);
		final NovaApi novaApi = mock(NovaApi.class);
		final ServerApi serverApi = mock(ServerApi.class);
		
		when(novaApi.getServerApiForZone(anyString())).thenReturn(serverApi);

		ServerOptionsFactory factory = mock(ServerOptionsFactory.class);
	
		HPCloudManager manager = spy(new HPCloudManager(credentials, service, novaApi)); 
		Whitebox.setInternalState(manager, "serverOptionsFactory", factory);
		
		HPCloudCreateServerRequest request = new HPCloudCreateServerRequest();
		request.nodeCount = 1;
		
		manager.createServerRequest(request);
		
		verify(serverApi).create(anyString(), anyString(), anyString(), any(CreateServerOptions.class));		
	}
	
	@Test
	public void callTwoServersCreationTest() {
		HPCloudCredentials credentials = mock(HPCloudCredentials.class);
		final ComputeService service = mock(ComputeService.class);
		final NovaApi novaApi = mock(NovaApi.class);
		final ServerApi serverApi = mock(ServerApi.class);
		
		when(novaApi.getServerApiForZone(anyString())).thenReturn(serverApi);
				
		ServerOptionsFactory factory = mock(ServerOptionsFactory.class);
	
		HPCloudManager manager = spy(new HPCloudManager(credentials, service, novaApi)); 
		Whitebox.setInternalState(manager, "serverOptionsFactory", factory);
		
		HPCloudCreateServerRequest request = new HPCloudCreateServerRequest();
		request.nodeCount = 2;
		
		manager.createServerRequest(request);
		
		verify(serverApi, times(2)).create(anyString(), anyString(), anyString(), any(CreateServerOptions.class));		
	}

}
