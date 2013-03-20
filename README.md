openstack-factory
=================

Native openstack virtual machine factory  

Eclipse Configuration
=====================

To setup the environment for running openstack factory is necessary to add the following Eclipse parameters into VM arguments:  
-Djclouds.trust-all-certs=true  
-Djclouds.keystone.credential-type=apiAccessKeyCredentials  
