package n3phele.factory.hpcloud;

import java.util.Date;
import java.util.Set;

import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.Server.Status;
import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.openstack.v2_0.domain.Resource;


public class HPCloudServer {
	private String												id;
	private String												name;
	private Set<Link>											links;
	private String												uuid;
	private String												tenantId;
	private String												userId;
	private Date												updated;
	private Date												created;
	private String												hostId;
	private String												accessIPv4;
	private String												accessIPv6;
	private Status												status;
	private Resource											image;
	private Resource											flavor;
	private String												keyName;
	private String												configDrive;
	/*private com.google.common.collect.Multimap<String, Address>	addresses;
	private Map<String, String>									metadata;
	private ServerExtendedAttributes							extendedStatus;
	private ServerExtendedAttributes							extendedAttributes;*/
	private String												diskConfig;
	
	public HPCloudServer()
	{
		
	}
	
	
}
