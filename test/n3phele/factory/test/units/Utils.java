package n3phele.factory.test.units;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import n3phele.service.model.core.NameValue;
import n3phele.service.model.core.VirtualServer;

public class Utils {
	
	public static VirtualServer createFakeDataVirtualServer() {
		URI uri = null;
		try {
			uri = new URI("http://localhost/");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		VirtualServer vs = new VirtualServer("", "", uri, new ArrayList<NameValue>(), uri, "1", "", "", "", uri, "");
		return vs;
	}
}
