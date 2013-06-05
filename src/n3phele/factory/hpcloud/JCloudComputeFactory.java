package n3phele.factory.hpcloud;

import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_SCRIPT_COMPLETE;

import java.util.Properties;
import java.util.concurrent.TimeUnit;
import static org.jclouds.Constants.*;

import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.gae.config.AsyncGoogleAppEngineConfigurationModule;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.rest.RestContext;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

public class JCloudComputeFactory implements CloudFactory {
	private ComputeService mCompute;	
	private NovaApi mNovaApi;

	/**
	 * @param hpCloudManager
	 * @param identity
	 *            A join of TenantName and AccessKey with a ":" between them.
	 * @param secretKey
	 *            A key associated with AccessKey provided in identity
	 *            parameter.
	 */
	public void initComputeService(String identity, String secretKey)
	{
		Properties properties = new Properties();
		long scriptTimeout = TimeUnit.MILLISECONDS.convert(20, TimeUnit.MINUTES);
		properties.setProperty(TIMEOUT_SCRIPT_COMPLETE, scriptTimeout + "");		
		properties.setProperty(PROPERTY_CONNECTION_TIMEOUT, 30000 + "");
		properties.setProperty(PROPERTY_REQUEST_TIMEOUT, 30000 + "");
		properties.setProperty("jclouds.modules","org.jclouds.gae.config.AsyncGoogleAppEngineConfigurationModule");
		properties.setProperty("jclouds.keystone.credential-type", "apiAccessKeyCredentials");
	
		Iterable<Module> modules = ImmutableSet.<Module> of(new SLF4JLoggingModule(), new AsyncGoogleAppEngineConfigurationModule());
		ContextBuilder builder = ContextBuilder.newBuilder(HPCloudManager.JCLOUD_PROVIDER).credentials(identity, secretKey).modules(modules).overrides(properties);
		ComputeServiceContext context = builder.buildView(ComputeServiceContext.class);
	
		mCompute = context.getComputeService();
		RestContext mNova = context.unwrap();
		mNovaApi = (NovaApi) mNova.getApi();
	}

	@Override
	public ComputeService getCompute() {
		return mCompute;
	}
	
	@Override
	public NovaApi getNovaApi() {
		return mNovaApi;
	}
	
}