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
import org.jclouds.openstack.nova.NovaAsyncClient;
import org.jclouds.openstack.nova.NovaClient;
import org.jclouds.openstack.nova.v2_0.compute.options.NovaTemplateOptions;
import org.jclouds.rest.RestContext;
import org.jclouds.sshj.config.SshjSshClientModule;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * @author Alexandre Leites
 *
 */
public class HPCloudManager {
	ComputeService mCompute;
	
	public static String JCLOUD_PROVIDER = "hpcloud-compute";
	
	
	/**
	 * @param creds HP Cloud credentials
	 */
	public HPCloudManager(HPCloudCredentials creds)
	{
		mCompute = initComputeService(creds.identity, creds.secretKey);
	}

	/**
	 * @param identity A join of TenantName and AccessKey with a ":" between them.
	 * @param secretKey A key associated with AccessKey provided in identity parameter.
	 * @return ComputeService component
	 */
	private ComputeService initComputeService(String identity, String secretKey)
	{
		Properties properties = new Properties();
		long scriptTimeout = TimeUnit.MILLISECONDS.convert(20, TimeUnit.MINUTES);
		properties.setProperty(TIMEOUT_SCRIPT_COMPLETE, scriptTimeout + "");

		Iterable<Module> modules = ImmutableSet.<Module> of(new SshjSshClientModule(), new SLF4JLoggingModule(), new EnterpriseConfigurationModule());

		ContextBuilder builder = ContextBuilder.newBuilder(JCLOUD_PROVIDER).credentials(identity, secretKey).modules(modules).overrides(properties);

		return builder.buildView(ComputeServiceContext.class).getComputeService();
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
		 * TODO: NovaTemplateOptions is used because we need to set to auto generate keypairs...
		 * Also, when you set the security groups here, they override the security group set on
		 * createNodesInGroup function. Needs to verify correctly this behavior.
		 * Security groups referred here needs to already exists and in createNodesInGroup they
		 * are created if doesn't exists.
		 */
		NovaTemplateOptions options = (NovaTemplateOptions)mCompute.templateOptions();
		//options.securityGroupNames("default");
		options.generateKeyPair(true);
		options.inboundPorts(22, 8887);
		
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
	
	public void createSecurityGroup()
	{
		RestContext<NovaClient, NovaAsyncClient> context = mCompute.getContext().getProviderSpecificContext();
		NovaClient client = context.getApi();
	}
}
