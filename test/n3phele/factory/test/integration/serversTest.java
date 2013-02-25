package n3phele.factory.test.integration;

import static org.junit.Assert.*;

import javax.ws.rs.core.MediaType;

import n3phele.factory.hpcloud.HPCloudCredentials;

import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import com.sun.jersey.test.framework.spi.container.TestContainerFactory;
import com.sun.jersey.test.framework.spi.container.grizzly.web.GrizzlyWebTestContainerFactory;

public class serversTest extends JerseyTest {

    public serversTest() throws Exception {
    	super(new WebAppDescriptor.Builder("n3phele.factory.rest").build());
    }
    
    @Override
    protected TestContainerFactory getTestContainerFactory() {
    	return new GrizzlyWebTestContainerFactory();
    }

    //FIXME: test examples here, change it.
    @Test
    public void testHelloWorld() {
        WebResource webResource = resource();
        String responseMsg = webResource.path("/servers").get(String.class);
        assertEquals("Hello World! Test!", responseMsg);
    }
    
    @Test
    public void testCreateServer() {
        WebResource webResource = resource();
        HPCloudCredentials credentials = new HPCloudCredentials("", "");
        ClientResponse clientResponse = webResource.path("/servers").type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, credentials);
        assertEquals(400, clientResponse.getStatus());
    }

}
