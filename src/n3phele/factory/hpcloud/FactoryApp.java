package n3phele.factory.hpcloud;

import java.io.Console;
import java.util.List;

import org.jclouds.compute.ComputeService;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.rest.RestContext;
import org.jclouds.compute.RunNodesException;

public class FactoryApp {
	HPCloudManager	hpcManager;

	public FactoryApp(String identity, String secretKey)
	{
		HPCloudCredentials credentials = new HPCloudCredentials(identity, secretKey);
		hpcManager = new HPCloudManager(credentials);
	}

	public void run()
	{
		HPCloudCreateServerRequest nodeCreationReq = new HPCloudCreateServerRequest();

		nodeCreationReq.hardwareId = "az-1.region-a.geo-1/100";
		nodeCreationReq.imageId = "az-1.region-a.geo-1/75845";
		nodeCreationReq.locationId = "az-1.region-a.geo-1";
		nodeCreationReq.nodeCount = 1;
		nodeCreationReq.securityGroup = "lis-lis-nodes-1";

		List<HPCloudServer> nodes = hpcManager.createServerRequest(nodeCreationReq);
		for (HPCloudServer srv : nodes)
		{
			System.out.printf(">> Node -> ID: %s Name: %s \n", srv.getId(), srv.getName());
		}
	}
}
