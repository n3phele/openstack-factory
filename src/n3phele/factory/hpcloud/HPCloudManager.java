/**
 * @author Alexandre Leites
 */
package n3phele.factory.hpcloud;

import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_SCRIPT_COMPLETE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import n3phele.factory.rest.impl.VirtualServerResource;

import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.domain.Location;
import org.jclouds.gae.config.AsyncGoogleAppEngineConfigurationModule;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Ingress;
import org.jclouds.openstack.nova.v2_0.domain.IpProtocol;
import org.jclouds.openstack.nova.v2_0.domain.KeyPair;
import org.jclouds.openstack.nova.v2_0.domain.RebootType;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroup;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.ServerCreated;
import org.jclouds.openstack.nova.v2_0.extensions.KeyPairApi;
import org.jclouds.openstack.nova.v2_0.extensions.SecurityGroupApi;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.nova.v2_0.options.CreateServerOptions;
import org.jclouds.rest.RestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * @author Alexandre Leites
 * 
 */
public class HPCloudManager {
	private ComputeService	mCompute;
	private RestContext		mNova;
	private NovaApi			mNovaApi;

	public static String	JCLOUD_PROVIDER	= "hpcloud-compute";
	final Logger logger = LoggerFactory.getLogger(HPCloudManager.class);

	/**
	 * @param creds
	 *            HP Cloud credentials
	 */
	public HPCloudManager(HPCloudCredentials creds)
	{
		logger.info("credential identity: "+creds.getIdentity());
		logger.info("credential secret key: "+creds.getSecretKey());
		initComputeService(creds.getIdentity(), creds.getSecretKey());
		
		
	}

	/**
	 * @param identity
	 *            A join of TenantName and AccessKey with a ":" between them.
	 * @param secretKey
	 *            A key associated with AccessKey provided in identity
	 *            parameter.
	 */
	private void initComputeService(String identity, String secretKey)
	{
		Properties properties = new Properties();		
		long scriptTimeout = TimeUnit.MILLISECONDS.convert(20, TimeUnit.MINUTES);
		properties.setProperty(TIMEOUT_SCRIPT_COMPLETE, scriptTimeout + "");
		properties.setProperty("jclouds.modules","org.jclouds.gae.config.AsyncGoogleAppEngineConfigurationModule");
		properties.setProperty("jclouds.keystone.credential-type", "apiAccessKeyCredentials");

		Iterable<Module> modules = ImmutableSet.<Module> of(new SLF4JLoggingModule(), new AsyncGoogleAppEngineConfigurationModule());

		ContextBuilder builder = ContextBuilder.newBuilder(JCLOUD_PROVIDER).credentials(identity, secretKey).modules(modules).overrides(properties);

		ComputeServiceContext context = builder.buildView(ComputeServiceContext.class);

		mCompute = context.getComputeService();
		mNova = context.unwrap();
		mNovaApi = (NovaApi) mNova.getApi();
	}

	/**
	 * @return list of available images into HP Cloud provider. This includes
	 *         user custom images too.
	 */
	public Set<? extends Image> listImages()
	{
		return mCompute.listImages();
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
	 * @param r
	 *            Represents our creation request
	 * @return a list of created nodes.
	 */
	public List<ServerCreated> createServerRequest(HPCloudCreateServerRequest r)
	{
		/**
		 * Get server async api
		 */
		ServerApi serverApi = mNovaApi.getServerApiForZone(r.locationId);
		
		/**
		 * Create our security group with following ports opened: TCP: 22, 8887
		 * UDP: None ICMP: Yes
		 */
		SecurityGroup secGroup = createSecurityGroup(r.securityGroup, r.locationId);
		
		/**
		 * Create our keypair. Return existent keypair if already exists.
		 */
		KeyPair keyPair = createKeyPair(r.keyName, r.locationId);
		
		/**
		 * Build our server creation options.
		 */
		CreateServerOptions options = new CreateServerOptions();
		options.securityGroupNames(secGroup.getName());
		options.keyPairName(keyPair.getName());
		
		/**
		 * Custom commands
		 */
		if( r.userData.length() > 0 )
			options.userData(r.userData.getBytes());
		
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
			
			ServerCreated server = serverApi.create(name, r.imageId, r.hardwareId, options);
			serversList.add(server);
		}

		return (serversList.size() > 0) ? serversList : null;
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
