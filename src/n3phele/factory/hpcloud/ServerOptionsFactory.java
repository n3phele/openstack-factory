package n3phele.factory.hpcloud;

import org.jclouds.openstack.nova.v2_0.domain.KeyPair;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroup;
import org.jclouds.openstack.nova.v2_0.options.CreateServerOptions;

public class ServerOptionsFactory {
	
	public CreateServerOptions buildCreateServerOptions( HPCloudManager manager,
			HPCloudCreateServerRequest r) {
		/**
		 * Create our security group with following ports opened: TCP: 22, 8887
		 * UDP: None ICMP: Yes
		 */
		SecurityGroup secGroup = manager.createSecurityGroup(r.security_groups, r.locationId);
		
		/**
		 * Create our keypair. Return existent keypair if already exists.
		 */
		KeyPair keyPair = manager.createKeyPair(r.keyName, r.locationId);
		
		/**
		 * Build our server creation options.
		 */
		CreateServerOptions options = new CreateServerOptions();
		options.securityGroupNames(secGroup.getName());
		options.keyPairName(keyPair.getName());
		
		/**
		 * Custom commands
		 */
		if(r.user_data!= null){
			if( r.user_data.length() > 0 )
				options.userData(r.user_data.getBytes());
		}
		return options;
	}
}
