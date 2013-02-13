package n3phele.factory.hpcloud;

import java.util.List;

public class FactoryApp {
	HPCloudManager	hpcManager;

	public FactoryApp(String identity, String secretKey)
	{
		hpcManager = new HPCloudManager(new HPCloudCredentials(identity, secretKey));
	}

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException
	{
		if (args.length < 2)
		{
			throw new IllegalArgumentException("Invalid number of parameters. Syntax is: identity(TenantName:AccessKey) secretKey");
		}
		String identity = args[0];
		String secretKey = args[1];
		FactoryApp factory = new FactoryApp(identity, secretKey);
		factory.run();
	}

	private void run()
	{
		HPCloudCreateServerRequest nodeCreationReq = new HPCloudCreateServerRequest();

		nodeCreationReq.hardwareId = "az-1.region-a.geo-1/100";
		nodeCreationReq.imageId = "az-1.region-a.geo-1/75845";
		nodeCreationReq.locationId = "az-1.region-a.geo-1";
		nodeCreationReq.nodeCount = 1;
		nodeCreationReq.securityGroup = "lis-lis-nodes";

		List<HPCloudServer> nodes = hpcManager.createServerRequest(nodeCreationReq);
		for (HPCloudServer srv : nodes)
		{
			System.out.printf(">> Node -> ID: %s Name: %s \n", srv.getId(), srv.getName());
		}
	}
}
