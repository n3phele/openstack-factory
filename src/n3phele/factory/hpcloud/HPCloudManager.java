/**
 * @author Alexandre Leites
 */
package n3phele.factory.hpcloud;

import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_SCRIPT_COMPLETE;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.domain.Location;
import org.jclouds.enterprise.config.EnterpriseConfigurationModule;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.compute.options.NovaTemplateOptions;
import org.jclouds.openstack.nova.v2_0.domain.Ingress;
import org.jclouds.openstack.nova.v2_0.domain.IpProtocol;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroup;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroupRule;
import org.jclouds.openstack.nova.v2_0.extensions.SecurityGroupApi;
import org.jclouds.rest.RestContext;
import org.jclouds.sshj.config.SshjSshClientModule;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * @author Alexandre Leites
 *
 */
public class HPCloudManager {
	private ComputeService mCompute;
	private RestContext mNova;
	private NovaApi mNovaApi;
	
	public static String JCLOUD_PROVIDER = "hpcloud-compute";
	
	
	/**
	 * @param creds HP Cloud credentials
	 */
	public HPCloudManager(HPCloudCredentials creds)
	{
		initComputeService(creds.identity, creds.secretKey);
	}

	/**
	 * @param identity A join of TenantName and AccessKey with a ":" between them.
	 * @param secretKey A key associated with AccessKey provided in identity parameter.
	 */
	private void initComputeService(String identity, String secretKey)
	{
		Properties properties = new Properties();
		long scriptTimeout = TimeUnit.MILLISECONDS.convert(20, TimeUnit.MINUTES);
		properties.setProperty(TIMEOUT_SCRIPT_COMPLETE, scriptTimeout + "");

		Iterable<Module> modules = ImmutableSet.<Module> of(new SshjSshClientModule(), new SLF4JLoggingModule(), new EnterpriseConfigurationModule());

		ContextBuilder builder = ContextBuilder.newBuilder(JCLOUD_PROVIDER).credentials(identity, secretKey).modules(modules).overrides(properties);
		
		ComputeServiceContext context = builder.buildView(ComputeServiceContext.class);

		mCompute = context.getComputeService();
		mNova = context.unwrap();
		mNovaApi = (NovaApi)mNova.getApi();
	}
	
	/**
	 * @return list of available images into HP Cloud provider. This includes user custom images too.
	 */
	public Set<? extends Image> listImages()
	{
		return mCompute.listImages();
	}
	
	/**
	 * @return list of available hardware profiles (flavors) into HP Cloud provider.
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
	 * @param nodeId our node identification.
	 */
	public void rebootNode(String nodeId)
	{
		mCompute.rebootNode(nodeId);
	}
	
	/**
	 * @param nodeId our node identification.
	 */
	public void terminateNode(String nodeId)
	{
		mCompute.destroyNode(nodeId);
	}
	
	/**
	 * @param nodeId our node identification.
	 */
	public void suspendNode(String nodeId)
	{
		mCompute.suspendNode(nodeId);
	}
	
	/**
	 * @param nodeId our node identification.
	 */
	public void resumeNode(String nodeId)
	{
		mCompute.resumeNode(nodeId);
	}
	
	/**
	 * @param r Represents our creation request
	 * @return a list of created nodes.
	 */
	public List<HPCloudServer> createServerRequest(HPCloudCreateServerRequest r)
	{
		TemplateBuilder templateBuilder = mCompute.templateBuilder();
		templateBuilder.imageId( r.imageId );
		templateBuilder.locationId( r.locationId );
		templateBuilder.hardwareId( r.hardwareId );
		
		/**
		 * Create our security group with following ports opened:
		 * TCP: 22, 8887
		 * UDP: None
		 * ICMP: Yes
		 */
		SecurityGroup secGroup = createSecurityGroup( r.securityGroup, r.locationId );
		
		/**
		 * After we created our security group, we need to setup our options.
		 * Here I define to our API create automatically an keyPair and put our nodes
		 * into newly created security group.
		 */
		NovaTemplateOptions options = (NovaTemplateOptions)mCompute.templateOptions();
		options.securityGroupNames(secGroup.getName());
		options.generateKeyPair(true);
		
		/**
		 * Finally, we build our options.
		 */
		templateBuilder.options(options);
		
		ArrayList<HPCloudServer> serversList = new ArrayList<HPCloudServer>();
		try
		{
			Set<? extends NodeMetadata> nodes = mCompute.createNodesInGroup(r.securityGroup, r.nodeCount, templateBuilder.build());
			for(NodeMetadata n : nodes)
			{
				HPCloudServer hpsrv = new HPCloudServer(n.getId(), n.getName(), n.getImageId(), n.getProviderId(), n.getHardware().getId(), n.getLocation().getId(), n.getBackendStatus(), n.getCredentials().getPrivateKey());
				serversList.add(hpsrv);
			}
		} catch (RunNodesException e)
		{
			//TODO: What us are expected to do here?
		}
		
		return serversList;
	}
	
	public SecurityGroup createSecurityGroup(String name, String zone)
	{
		SecurityGroupApi sgApi = mNovaApi.getSecurityGroupExtensionForZone(zone).get();
		
		String groupName = "n3phele-" + name;
		SecurityGroup sg = sgApi.createWithDescription(groupName, "Created by n3phele.");
		
		/**
		 * External rules
		 */
		sgApi.createRuleAllowingCidrBlock(sg.getId(), Ingress.builder().ipProtocol(IpProtocol.TCP).fromPort(22).toPort(22).build(), "0.0.0.0/0");
		sgApi.createRuleAllowingCidrBlock(sg.getId(), Ingress.builder().ipProtocol(IpProtocol.TCP).fromPort(8887).toPort(8887).build(), "0.0.0.0/0");
		sgApi.createRuleAllowingCidrBlock(sg.getId(), Ingress.builder().ipProtocol(IpProtocol.ICMP).fromPort(-1).toPort(-1).build(), "0.0.0.0/0");
		
		/**
		 * Internal rules. Allowing nodes access each other.
		 * TODO: It doesn't allow string, just integer, needs to verify how to do
		 */
		/*sgApi.createRuleAllowingSecurityGroupId(sg.getId(), Ingress.builder().ipProtocol(IpProtocol.TCP).fromPort(1).toPort(65535).build(), groupName);
		sgApi.createRuleAllowingSecurityGroupId(sg.getId(), Ingress.builder().ipProtocol(IpProtocol.UDP).fromPort(1).toPort(65535).build(), groupName);
		sgApi.createRuleAllowingSecurityGroupId(sg.getId(), Ingress.builder().ipProtocol(IpProtocol.ICMP).fromPort(-1).toPort(-1).build(), groupName);*/
		
		return sg;
	}
}
