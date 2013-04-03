package n3phele.factory.test.units;

import javax.ws.rs.core.SecurityContext;

import static org.mockito.Mockito.*;

import n3phele.factory.rest.impl.BasicSecurityFilter;
import n3phele.service.core.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.jersey.api.container.MappableContainerException;
import com.sun.jersey.core.util.Base64;
import com.sun.jersey.spi.container.ContainerRequest;

public class SecurityFilterTest {

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
	public void testFilterCorrectUser() {
		BasicSecurityFilter securityFilter = new BasicSecurityFilter() {
			@Override
			protected boolean _isSecure()
			{
				return true;
			}		
		};
				
		//Mock the container Request with authentication
		ContainerRequest container = mock(ContainerRequest.class);
		String correctUsername = Resource.get("factoryUser", "");
		String correctPassword = Resource.get("factorySecret", "");
		
		//Use basic auth format
		String authenticationHeader = correctUsername + ":" + correctPassword;
		String encoded64 = new String(Base64.encode(authenticationHeader));
		authenticationHeader = new String("Basic " + encoded64);
		when(container.getHeaderValue(anyString())).thenReturn(authenticationHeader);		
				
		securityFilter.filter(container);
		
		verify(container).setSecurityContext((SecurityContext)any());
	}
	
	@Test(expected = MappableContainerException.class)
	public void testFilterBadCredentials() {
		BasicSecurityFilter securityFilter = new BasicSecurityFilter() {
			@Override
			protected boolean _isSecure()
			{
				return true;
			}		
		};
		
		//Mock the container Request with authentication
		ContainerRequest container = mock(ContainerRequest.class);
		String correctUsername = "auser";
		String correctPassword = "apassword";
		
		//Use basic auth format
		String authenticationHeader = correctUsername + ":" + correctPassword;
		String encoded64 = new String(Base64.encode(authenticationHeader));
		authenticationHeader = new String("Basic " + encoded64);
		when(container.getHeaderValue(anyString())).thenReturn(authenticationHeader);	
				
		securityFilter.filter(container);
		
		verify(container).setSecurityContext((SecurityContext)any());
	}


}
