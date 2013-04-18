package n3phele.factory.hpcloud;

import org.jclouds.compute.ComputeService;
import org.jclouds.openstack.nova.v2_0.NovaApi;

public interface CloudFactory {

	public ComputeService getCompute();

	public NovaApi getNovaApi();
	
	public void initComputeService(String identity, String secretKey);

}