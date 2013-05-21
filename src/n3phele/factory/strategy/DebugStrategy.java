package n3phele.factory.strategy;

import java.util.HashMap;
import java.util.Map;

import n3phele.factory.hpcloud.HPCloudManager;
import n3phele.factory.rest.impl.VirtualServerResource;
import n3phele.service.core.Resource;
import n3phele.service.model.core.VirtualServer;
import n3phele.service.model.core.VirtualServerStatus;

public class DebugStrategy {

	public void makeDebug(VirtualServer virtualServer, VirtualServerResource resource, HPCloudManager hpCloudManager) throws Exception
	{
		String instanceId = virtualServer.getInstanceId();
		updateCloudVMInfoAsDebug(virtualServer, resource, hpCloudManager);
		
		updateVMState(virtualServer, resource);

		/**
		 * Create a new zombie virtualServer object, and then set item
		 * instance Id to null. Update item. Update status.
		 */
		VirtualServer clone = new VirtualServer("debug", virtualServer.getDescription(), virtualServer.getLocation(), virtualServer.getParameters(), null, virtualServer.getAccessKey(), virtualServer.getEncryptedKey(), virtualServer.getOwner(), virtualServer.getIdempotencyKey());
		clone.setCreated(virtualServer.getCreated());
		clone.setInstanceId(instanceId);

		resource.add(clone);
	}

	protected void updateVMState(VirtualServer virtualServer,
			VirtualServerResource resource) {
		virtualServer.setInstanceId(null);
		virtualServer.setZombie(true);
		resource.updateStatus(virtualServer, VirtualServerStatus.terminated);
		resource.update(virtualServer);
	}

	protected void updateCloudVMInfoAsDebug(VirtualServer virtualServer,
			VirtualServerResource resource, HPCloudManager hpCloudManager) {
		/**
		 * Now we're using metadata to set instance behavior (zombie or debug)
		 */
		String locationId = resource.getLocationId(virtualServer);
		Map<String, String> tags = new HashMap<String, String>();
		tags.put("n3phele-name", virtualServer.getName());
		tags.put("n3phele-behavior", "debug");
		tags.put("n3phele-factory", Resource.get("factoryName", resource.FACTORY_NAME));
		tags.put("n3phele-uri", "");

		hpCloudManager.putServerTags(virtualServer.getInstanceId(), locationId, tags);
	}

}
