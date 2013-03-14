package n3phele.factory.test.units;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import n3phele.factory.rest.impl.HPCloudExtractor;
import n3phele.service.model.core.NameValue;

import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.ServerExtendedAttributes;
import org.jclouds.openstack.nova.v2_0.domain.ServerExtendedStatus;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Optional;

public class HPCloudExtractorTest {
	

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
	public void testExtractParameter(){
		
		Server server = Mockito.mock(Server.class);
		
		when(server.getAccessIPv4()).thenReturn("192.186.100.102");	
		
		ArrayList<NameValue> testReturn = HPCloudExtractor.extract(server);
		
		NameValue expected = new NameValue("accessIPv4","192.186.100.102");
		NameValue returned = new NameValue();
		for(int i = 0; i < testReturn.size(); i++){
			if(testReturn.get(i).getKey().compareTo("accessIPv4")==0){
				returned = testReturn.get(i);
				break;
			}
		}		
		Assert.assertEquals(expected,returned);				
	}
	
	@Test
	public void testIgnoreMetadata(){
		
		Server server = Mockito.mock(Server.class);
		
		 Map<String,String> map=new HashMap<String, String>();
		 map.put("test", "test");
		 
		 when(server.getMetadata()).thenReturn(map);
		 
		 ArrayList<NameValue> testReturn = HPCloudExtractor.extract(server);
		 
		 NameValue expected = new NameValue();
		 NameValue returned = new NameValue();
		 for(int i = 0; i < testReturn.size(); i++){
				if(testReturn.get(i).getKey().compareTo("metadata")==0){
					returned = testReturn.get(i);
					break;
				}
			}	
		 
		Assert.assertEquals(expected,returned);	
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testPowerState(){
		
		Server server = Mockito.mock(Server.class);
	
		Optional<ServerExtendedStatus> opt = Mockito.mock(Optional.class);
		
		ServerExtendedStatus extStatus = Mockito.mock(ServerExtendedStatus.class);		
		
		when(opt.get()).thenReturn(extStatus);		
		
		when(extStatus.getPowerState()).thenReturn(1);
		
		when(server.getExtendedStatus()).thenReturn(opt);
		
		ArrayList<NameValue> testReturn = HPCloudExtractor.extract(server);
		
		 NameValue expected = new NameValue("PowerState","1");
		 NameValue returned = new NameValue();

		 for(int i = 0; i < testReturn.size(); i++){
				if(testReturn.get(i).getKey().compareTo("PowerState")==0){
					returned = testReturn.get(i);
					break;
				}
		}
		 
		Assert.assertEquals(expected,returned);	
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testTaskState(){
		
		Server server = Mockito.mock(Server.class);
		
		Optional<ServerExtendedStatus> opt = Mockito.mock(Optional.class);
		
		ServerExtendedStatus extStatus = Mockito.mock(ServerExtendedStatus.class);		
		
		when(opt.get()).thenReturn(extStatus);		
		
		when(extStatus.getTaskState()).thenReturn("task");
		
		when(server.getExtendedStatus()).thenReturn(opt);
		
		ArrayList<NameValue> testReturn = HPCloudExtractor.extract(server);
		
		 NameValue expected = new NameValue("TaskState","task");
		 NameValue returned = new NameValue();

		 for(int i = 0; i < testReturn.size(); i++){
				if(testReturn.get(i).getKey().compareTo("TaskState")==0){
					returned = testReturn.get(i);
					break;
				}
		}
		 
		Assert.assertEquals(expected,returned);	
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testVMState(){
		
		Server server = Mockito.mock(Server.class);
		
		Optional<ServerExtendedStatus> opt = Mockito.mock(Optional.class);
		
		ServerExtendedStatus extStatus = Mockito.mock(ServerExtendedStatus.class);		
		
		when(opt.get()).thenReturn(extStatus);	
		
		when(extStatus.getVmState()).thenReturn("state");
		
		when(server.getExtendedStatus()).thenReturn(opt);
		
		ArrayList<NameValue> testReturn = HPCloudExtractor.extract(server);
		
		NameValue expected = new NameValue("VmState","state");
		NameValue returned = new NameValue();
		 
		 for(int i = 0; i < testReturn.size(); i++){
				if(testReturn.get(i).getKey().compareTo("VmState")==0){
					returned = testReturn.get(i);
					break;
				}
		}
		 
		Assert.assertEquals(expected,returned);	
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testInstanceName(){
		
		Server server = Mockito.mock(Server.class);
		
		Optional<ServerExtendedAttributes> sat = Mockito.mock(Optional.class);
		
		ServerExtendedAttributes extAtt = Mockito.mock(ServerExtendedAttributes.class);
		
		when(sat.get()).thenReturn(extAtt);	
		
		when(extAtt.getInstanceName()).thenReturn("name");
		
		when(server.getExtendedAttributes()).thenReturn(sat);
		
		ArrayList<NameValue> testReturn = HPCloudExtractor.extract(server);
		
		NameValue expected = new NameValue("InstanceName","name");
		NameValue returned = new NameValue();
		 
		 for(int i = 0; i < testReturn.size(); i++){
				if(testReturn.get(i).getKey().compareTo("InstanceName")==0){
					returned = testReturn.get(i);
					break;
				}
		}
		 
		Assert.assertEquals(expected,returned);	
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testHostName(){
		
		Server server = Mockito.mock(Server.class);
		
		Optional<ServerExtendedAttributes> sat = Mockito.mock(Optional.class);
		
		ServerExtendedAttributes extAtt = Mockito.mock(ServerExtendedAttributes.class);
		
		when(sat.get()).thenReturn(extAtt);	
		
		when(extAtt.getHostName()).thenReturn("hostName");
		
		when(server.getExtendedAttributes()).thenReturn(sat);
		
		ArrayList<NameValue> testReturn = HPCloudExtractor.extract(server);
		
		NameValue expected = new NameValue("HostName","hostName");
		NameValue returned = new NameValue();
		 
		 for(int i = 0; i < testReturn.size(); i++){
				if(testReturn.get(i).getKey().compareTo("HostName")==0){
					returned = testReturn.get(i);
					break;
				}
		}
		 
		Assert.assertEquals(expected,returned);	
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testHypervisorHostName(){
		
		Server server = Mockito.mock(Server.class);
		
		Optional<ServerExtendedAttributes> sat = Mockito.mock(Optional.class);
		
		ServerExtendedAttributes extAtt = Mockito.mock(ServerExtendedAttributes.class);
		
		when(sat.get()).thenReturn(extAtt);	
		
		when(extAtt.getHypervisorHostName()).thenReturn("hypervisorHostName");
		
		when(server.getExtendedAttributes()).thenReturn(sat);
		
		ArrayList<NameValue> testReturn = HPCloudExtractor.extract(server);
		
		NameValue expected = new NameValue("HypervisorHostName","hypervisorHostName");
		NameValue returned = new NameValue();
		
		 
		 for(int i = 0; i < testReturn.size(); i++){
				if(testReturn.get(i).getKey().compareTo("HypervisorHostName")==0){
					returned = testReturn.get(i);
					break;
				}
		}
		 
		Assert.assertEquals(expected,returned);	
	}
}
