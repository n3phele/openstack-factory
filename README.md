openstack-factory
=================

Native openstack virtual machine factory

Eclipse Configuration
=====================

To set up the environment is necessary to import the current code and configure the following Eclipse parameters:

Main Class: n3phele.factory.hpcloud.FactoryApp
Program Arguments: TenantName:AccessKey secretKey (This parameters is from your HPCloud account).
VM Arguments: -Djclouds.trust-all-certs=true
