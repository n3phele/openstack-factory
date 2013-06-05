package n3phele.factory.hpcloud;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.domain.Location;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Image;
import org.jclouds.openstack.nova.v2_0.domain.Ingress;
import org.jclouds.openstack.nova.v2_0.domain.IpProtocol;
import org.jclouds.openstack.nova.v2_0.domain.KeyPair;
import org.jclouds.openstack.nova.v2_0.domain.RebootType;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroup;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.ServerCreated;
import org.jclouds.openstack.nova.v2_0.extensions.KeyPairApi;
import org.jclouds.openstack.nova.v2_0.extensions.SecurityGroupApi;
import org.jclouds.openstack.nova.v2_0.features.ImageApi;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.nova.v2_0.options.CreateServerOptions;
import org.jclouds.openstack.nova.v2_0.options.RebuildServerOptions;

import com.google.common.collect.FluentIterable;

/**
 * @author Alexandre Leites
 * 
 */
public class HPCloudManager {
	public static String	JCLOUD_PROVIDER	= "hpcloud-compute";
	
	private ComputeService mCompute;
	private NovaApi mNovaApi;
	
	private ServerOptionsFactory serverOptionsFactory;

	/**
	 * @param creds
	 *            HP Cloud credentials
	 */
	public HPCloudManager(HPCloudCredentials creds)
	{
		CloudFactory jCloudCompute = new JCloudComputeFactory();
		jCloudCompute.initComputeService(creds.getIdentity(), creds.getSecretKey());
		
		mCompute = jCloudCompute.getCompute();
		mNovaApi = jCloudCompute.getNovaApi();
		
		serverOptionsFactory = new ServerOptionsFactory();
	}
	
	public HPCloudManager(HPCloudCredentials creds, ComputeService compute, NovaApi novaApi)
	{
		this.mCompute = compute;
		this.mNovaApi = novaApi;
		serverOptionsFactory = new ServerOptionsFactory();
	}
	
	/**
	 * @return list of available hardware profiles (flavors) into HP Cloud
	 *         provider.
	 */
	public Set<? extends Hardware> listHardwareProfiles()
	{
		return mCompute.listHardwareProfiles();
	}

	/**
	 * @return list of available locations into HP Cloud provider.
	 */
	public Set<? extends Location> listLocations()
	{
		return mCompute.listAssignableLocations();
	}

	/**
	 * @return list of user nodes (servers).
	 */
	public Set<? extends ComputeMetadata> listNodes()
	{
		return mCompute.listNodes();
	}

	/**
	 * @param nodeId
	 *            our node identification.
	 */
	public void suspendNode(String nodeId)
	{
		mCompute.suspendNode(nodeId);
	}

	/**
	 * @param nodeId
	 *            our node identification.
	 */
	public void resumeNode(String nodeId)
	{
		mCompute.resumeNode(nodeId);
	}
	
	/**
	 * @param zone
	 * @param nodeId our node identification.
	 * @param rebootType 
	 */
	public void rebootNode(String zone, String nodeId, RebootType rebootType)
	{
		/**
		 * Get server async api
		 */
		ServerApi serverApi = mNovaApi.getServerApiForZone(zone);
		
		serverApi.reboot(nodeId, rebootType);
	}
	
	/**
	 * @param zone
	 * @param nodeId our node identification.
	 * @param rebuildType 
	 */
	public void rebuildNode(String zone, String nodeId)
	{ 
		RebuildServerOptions rebuildType = new RebuildServerOptions();
		/**
		 * Get server async api
		 */
		ServerApi serverApi = mNovaApi.getServerApiForZone(zone);
		
		Server s = getServerById(zone, nodeId);
		rebuildType.withImage(s.getImage().getId().toString());
		serverApi.rebuild(nodeId, rebuildType);
	}

	/**
	 * @param zone 
	 * @param nodeId our node identification.
	 */
	public boolean terminateNode(String zone, String nodeId)
	{
		/**
		 * Get server async api
		 */
		ServerApi serverApi = mNovaApi.getServerApiForZone(zone);
		
		return serverApi.delete(nodeId);
	}
	
	/**
	 * 
	 * @param zone Compute zone
	 * @param Id Server Id
	 * @return Server object
	 */
	public Server getServerById(String zone, String Id)
	{
		/**
		 * Get server async api
		 */
		ServerApi serverApi = mNovaApi.getServerApiForZone(zone);
		
		return serverApi.get(Id);
	}
	
	/**
	 * 
	 * @param zone Compute zone
	 * @param Id Image Id
	 * @return Image object
	 */
	public Image getImageById(String zone, String Id)
	{
		/**
		 * Get image async api
		 */
		ImageApi imageApi = mNovaApi.getImageApiForZone(zone);
		
		return imageApi.get(Id);
	}

	/**
	 * @param r
	 *            Represents our creation request
	 * @return a list of created nodes.
	 */
	public List<ServerCreated> createServerRequest(HPCloudCreateServerRequest r)
	{
		/**
		 * Get server async api
		 */
		ServerApi serverApi = getServerApi(r);
		
		CreateServerOptions options = serverOptionsFactory.buildCreateServerOptions(this,r);
		
		/**
		 * Append n3phele prefix
		 */
		if( !r.serverName.startsWith("n3phele-") )
			r.serverName = "n3phele-" + r.serverName;
		
		/**
		 * Send our requests to HPCloud
		 */
		ArrayList<ServerCreated> serversList = new ArrayList<ServerCreated>();
		
		for(int i=0; i < r.nodeCount; i++)
		{
			String name = r.serverName;
			
			/**
			 * If we're building more than one server, append a number into name
			 */
			if( r.nodeCount > 1 )
				name = name.concat("-" + String.valueOf(i));
			
			ServerCreated server = serverApi.create(name, r.imageRef, r.flavorRef, options);
			serversList.add(server);
		}

		return (serversList.size() > 0) ? serversList : null;
	}

	protected ServerApi getServerApi(HPCloudCreateServerRequest r) {
		return mNovaApi.getServerApiForZone(r.locationId);
	}

	
	/**
	 * Check is a security group exists
	 */
	public boolean checkSecurityGroup(String name, String zone)
	{
		SecurityGroupApi sgApi = mNovaApi.getSecurityGroupExtensionForZone(zone).get();
		String groupName;
		
		if( !name.startsWith("n3phele-") )
			groupName = "n3phele-" + name;
		else
			groupName = name;
		
		FluentIterable<? extends SecurityGroup> sgList = sgApi.list();
		for (SecurityGroup sg : sgList)
		{
			if(sg.getName().equals(groupName))
				return true;
		}
		
		return false;		
	}

	/**
	 * Create a security group or return the existent one
	 * @param name security group name
	 * @param zone serverApi zone
	 * @return
	 */
	public SecurityGroup createSecurityGroup(String name, String zone)
	{
		SecurityGroup sg = null;
		SecurityGroupApi sgApi = mNovaApi.getSecurityGroupExtensionForZone(zone).get();
		String groupName;
		
		if( !name.startsWith("n3phele-") )
			groupName = "n3phele-" + name;
		else
			groupName = name;

		try
		{
			sg = sgApi.createWithDescription(groupName, "Created by n3phele.");

			/**
			 * External rules
			 */
			sgApi.createRuleAllowingCidrBlock(sg.getId(), Ingress.builder().ipProtocol(IpProtocol.TCP).fromPort(22).toPort(22).build(), "0.0.0.0/0");
			sgApi.createRuleAllowingCidrBlock(sg.getId(), Ingress.builder().ipProtocol(IpProtocol.TCP).fromPort(8887).toPort(8887).build(), "0.0.0.0/0");
			sgApi.createRuleAllowingCidrBlock(sg.getId(), Ingress.builder().ipProtocol(IpProtocol.ICMP).fromPort(-1).toPort(-1).build(), "0.0.0.0/0");

			/**
			 * Internal rules. Allowing nodes access each other. TODO: It
			 * doesn't allow string, just integer, needs to verify how to do
			 */
			/*
			 * sgApi.createRuleAllowingSecurityGroupId(sg.getId(), Ingress.builder().ipProtocol(IpProtocol.TCP).fromPort(1).toPort(65535).build(), groupName);
			 * sgApi.createRuleAllowingSecurityGroupId(sg.getId(), Ingress.builder().ipProtocol(IpProtocol.UDP).fromPort(1).toPort(65535).build(), groupName);
			 * sgApi.createRuleAllowingSecurityGroupId(sg.getId(), Ingress.builder().ipProtocol(IpProtocol.ICMP).fromPort(-1).toPort(-1).build(), groupName);
			 */
		} catch (Exception e)
		{
			// TODO: What us are expected to do here?
			FluentIterable<? extends SecurityGroup> groupList = sgApi.list();
			for (SecurityGroup sg2 : groupList)
			{
				if (sg2.getName().equals(groupName))
					return sg2;
			}
		}

		return sg;
	}
	
	public int getKeyPairsCount(String zone)
	{
		KeyPairApi kpApi = mNovaApi.getKeyPairExtensionForZone(zone).get();
		
		FluentIterable<? extends KeyPair> kpList = kpApi.list();
		
		return kpList.size();
	}
	
	public boolean checkKeyPair(String name, String zone)
	{
		KeyPairApi kpApi = mNovaApi.getKeyPairExtensionForZone(zone).get();
		String kpName;
		
		if( !name.startsWith("n3phele-") )
			kpName = "n3phele-" + name;
		else
			kpName = name;
		
		FluentIterable<? extends KeyPair> kpList = kpApi.list();
		for (KeyPair kp : kpList)
		{
			if(kp.getName().equals(kpName))
				return true;
		}
		
		return false;
	}

	/**
	 * Create a KeyPair with desired name
	 * 
	 * @param name KeyPair name
	 * @param zone KeyPair zone
	 * @return KeyPair
	 */
	public KeyPair createKeyPair(String name, String zone)
	{
		KeyPairApi kpApi = mNovaApi.getKeyPairExtensionForZone(zone).get();
		String kpName;
		KeyPair kp = null;
		
		if( !name.startsWith("n3phele-") )
			kpName = "n3phele-" + name;
		else
			kpName = name;

		try
		{
			kp = kpApi.create(kpName);
		} catch (Exception e)
		{
			FluentIterable<? extends KeyPair> kpList = kpApi.list();
			for (KeyPair kp2 : kpList)
			{
				if(kp2.getName().equals(kpName))
					return kp2;
			}
		}

		return kp;
	}

	public void putServerTags(String instanceId, String locationId, Map<String, String> tags)
	{
		/**
		 * Get server async api
		 */
		ServerApi serverApi = mNovaApi.getServerApiForZone(locationId);
		
		serverApi.updateMetadata(instanceId, tags);
	}
}
