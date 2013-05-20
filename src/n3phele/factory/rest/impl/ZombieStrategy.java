package n3phele.factory.rest.impl;

import java.util.HashMap;
import java.util.Map;

import n3phele.factory.hpcloud.HPCloudManager;
import n3phele.service.core.Resource;
import n3phele.service.model.core.VirtualServer;
import n3phele.service.model.core.VirtualServerStatus;

import org.jclouds.openstack.nova.v2_0.domain.RebootType;

public class ZombieStrategy {

	public void makeZombie(VirtualServer virtualServer, VirtualServerResource resource, HPCloudManager hpCloudManager) throws Exception
	{
		String instanceId = virtualServer.getInstanceId();
		try
		{
			virtualServer.setInstanceId(null);
			virtualServer.setZombie(true);
			resource.updateStatus(virtualServer, VirtualServerStatus.terminated);
			resource.update(virtualServer);

			/**
			 * Now we're using metadata to set instance behavior (zombie or debug)
			 */
			String locationId = resource.getLocationId(virtualServer);
			Map<String, String> tags = new HashMap<String, String>();
			tags.put("n3phele-name", virtualServer.getName());
			tags.put("n3phele-behavior", "zombie");
			tags.put("n3phele-factory", Resource.get("factoryName", resource.FACTORY_NAME));
			tags.put("n3phele-uri", "");

			hpCloudManager.putServerTags(virtualServer.getInstanceId(), locationId, tags);			
			hpCloudManager.rebootNode(locationId, virtualServer.getInstanceId(), RebootType.SOFT);

			/**
			 * Create a new zombie virtualServer object, and then set item
			 * instance Id to null. Update item. Update status.
			 */
			VirtualServer cloneZombie = new VirtualServer("zombie", virtualServer.getDescription(), virtualServer.getLocation(), virtualServer.getParameters(), null, virtualServer.getAccessKey(), virtualServer.getEncryptedKey(), virtualServer.getOwner(), virtualServer.getIdempotencyKey());
			cloneZombie.setCreated(virtualServer.getCreated());
			cloneZombie.setInstanceId(instanceId);

			/**
			 * The add operation does two writes in order to fix the reference
			 * URI.
			 * This creates a race condition for a fetch based on name of zombie
			 * Similarly, refresh amd update could cause a race condition.
			 * Updates semantics
			 * need to be strengthened to fail if the object is not in the
			 * store, and the check and write wrapped in
			 * a transaction.
			 */
			resource.add(cloneZombie);
		} catch (Exception e)
		{
			resource.logger.error("makeZombie delete of instanceId " + instanceId, e);
			resource.deleteInstance(virtualServer);
			throw e;
		}

	}

}
