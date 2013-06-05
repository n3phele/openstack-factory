package n3phele.factory.test.units;

import static org.junit.Assert.*;
import junit.framework.Assert;

import n3phele.factory.hpcloud.HPCloudCreateServerRequest;
import n3phele.factory.hpcloud.HPCloudManager;
import n3phele.factory.hpcloud.ServerOptionsFactory;

import org.jclouds.openstack.nova.v2_0.domain.KeyPair;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroup;
import org.jclouds.openstack.nova.v2_0.options.CreateServerOptions;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.internal.verification.Times;

import static org.mockito.Mockito.*;

public class ServerOptionsFactoryTest {

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
	public void createServerOptionsTest() {
		ServerOptionsFactory factory = new ServerOptionsFactory();
		
		HPCloudManager manager = mock(HPCloudManager.class);
		SecurityGroup group = mock(SecurityGroup.class);
		when(group.getName()).thenReturn("securityGroup");		
		KeyPair key = mock(KeyPair.class);
		when(key.getName()).thenReturn("keyName");
		
		when(manager.createKeyPair(anyString(), anyString())).thenReturn(key);
		when(manager.createSecurityGroup(anyString(), anyString())).thenReturn(group);
		
		HPCloudCreateServerRequest request = new HPCloudCreateServerRequest();
		request.user_data= null;
		request.security_groups = "securityGroup";
		request.locationId = "locationId";
		request.keyName = "keyName";
		
		CreateServerOptions options = factory.buildCreateServerOptions(manager, request); 		
		
		verify(manager, times(1)).createSecurityGroup("securityGroup", "locationId");
		verify(manager, times(1)).createKeyPair("keyName", "locationId");
		
		Assert.assertEquals( "securityGroup", options.getSecurityGroupNames().toArray()[0]);
		Assert.assertEquals( "keyName", options.getKeyPairName());
	}

}
