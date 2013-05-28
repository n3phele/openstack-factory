package n3phele.factory.test.units;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import n3phele.factory.model.ServiceModelDao;
import n3phele.service.core.Resource;
import n3phele.service.model.core.AbstractManager;
import n3phele.service.model.core.GenericModelDao;
import n3phele.service.model.core.VirtualServer;

//Helper that communicate with database for virtual server objects
public class VirtualServerTestDAO extends AbstractManager<VirtualServer> {

	@Override
	protected URI myPath()
	{
		return UriBuilder.fromUri(Resource.get("baseURI", "http://localhost:8889/resources")).path("virtualServer").build();
	}
	
	@Override
	public GenericModelDao<VirtualServer> itemDaoFactory()
	{
		return new ServiceModelDao<VirtualServer>(VirtualServer.class);
	}
	
	protected void add (VirtualServer vs){
		super.add(vs);
	}
	
	protected void delete (VirtualServer vs){
		super.delete(vs);
	}
	
	protected VirtualServer get(Long id){
		return super.get(id);
	}
}
