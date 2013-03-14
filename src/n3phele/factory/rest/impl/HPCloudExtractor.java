/**
 * @author Nigel Cook
 *
 * (C) Copyright 2010-2012. Nigel Cook. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 * Licensed under the terms described in LICENSE file that accompanied this code, (the "License"); you may not use this file
 * except in compliance with the License. 
 * 
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on 
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 *  specific language governing permissions and limitations under the License.
 */

package n3phele.factory.rest.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


import n3phele.service.model.core.NameValue;

import org.jclouds.openstack.nova.v2_0.domain.Address;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.ServerExtendedAttributes;
import org.jclouds.openstack.nova.v2_0.domain.ServerExtendedStatus;

import com.google.common.collect.Multimap;

/** Introspects an object extracting name/value pairs.
 * @author Nigel Cook
 *
 */
public class HPCloudExtractor {
	private final static Logger log = Logger.getLogger(HPCloudExtractor.class.getName());  
	/** Extract object name/value pairs by introspection. The processing looks for object methods that being with "get" and
	 * have no arguments. The methods are invoked and if a non-null result is returned, then the method name with "get" removed
	 * and the string version of the result form a name/value pair.
	 * @param o object to be introspected
	 * @return list of extracted name/value pairs
	 */
	public static ArrayList<NameValue> extract(Server o) {
		ArrayList<NameValue> result = new ArrayList<NameValue>();
		Method methods[] = o.getClass().getMethods();
		for(Method method : methods) {
			if(method.getName().startsWith("get")) {
				String field = method.getName().substring("get".length());
				//Ignore map of metadata
				if((field.compareTo("Metadata"))!=0){
					//Retrieve private and public IP addresses from multimap
					if((field.compareTo("Addresses")==0)){						
						Class<?> args[] = method.getParameterTypes();
						if(args.length == 0) {
							Class<?> target = List.class;
							try {
								Object response = method.invoke(o);
								if(response != null) {									
									String valuePrivate = getPrivateAddresses(response);
									String valuePublic = getPublicAddresses(response);
									result.add(new NameValue("PrivateIPAddresses", valuePrivate));
									result.add(new NameValue("PublicIPAddresses", valuePublic));
									log.info("Added field PrivateIPAddresses of type "+List.class+" with value "+valuePrivate);
									log.info("Added field PublicIPAddresses of type "+List.class+" with value "+valuePublic);
								}
							} catch(Exception e) {
								log.log(Level.WARNING,method.getName()+" with return "+target.getCanonicalName(), e);
							}
							
						}
					}
					//Retrieve info from ServerExtendedStatus object
					else if((field.compareTo("ServerExtendedStatus")==0)){
						Class<?> args[] = method.getParameterTypes();
						if(args.length == 0) {
							Class<?> target = ServerExtendedStatus.class;
							try {
								Object response = method.invoke(o);
								if(response != null) {									
									Long powerState = getPowerState(response);
									String taskState = getTaskState(response);
									String vmState = getVMState(response);
									result.add(new NameValue("PowerState", powerState.toString()));
									result.add(new NameValue("TaskState", taskState));
									result.add(new NameValue("VMState", vmState));
									log.info("Added field PowerState of type "+ServerExtendedStatus.class+" with value "+powerState.toString());
									log.info("Added field TaskState of type "+ServerExtendedStatus.class+" with value "+taskState);
									log.info("Added field VMState of type "+ServerExtendedStatus.class+" with value "+vmState);
								}
							} catch(Exception e) {
								log.log(Level.WARNING,method.getName()+" with return "+target.getCanonicalName(), e);
							}
						}						
					}
					//Retrieve info from ServerExtendedAttributes object
					else if((field.compareTo("ServerExtendedAttributes")==0)){
						Class<?> args[] = method.getParameterTypes();
						if(args.length == 0) {
							Class<?> target = ServerExtendedAttributes.class;
							try {
								Object response = method.invoke(o);
								if(response != null) {									
									String instanceName = getInstanceName(response);
									String hostName = getHostName(response);
									String hypName = getHypervisorHostName(response);
									result.add(new NameValue("InstanceName", instanceName));
									result.add(new NameValue("HostName", hostName));
									result.add(new NameValue("HypervisorHostName", hypName));
									log.info("Added field PowerState of type "+ServerExtendedAttributes.class+" with value "+instanceName);
									log.info("Added field TaskState of type "+ServerExtendedAttributes.class+" with value "+hostName);
									log.info("Added field VMState of type "+ServerExtendedAttributes.class+" with value "+hypName);
								}
							} catch(Exception e) {
								log.log(Level.WARNING,method.getName()+" with return "+target.getCanonicalName(), e);
							}
						}	
					}else{
						
						Class<?> args[] = method.getParameterTypes();
						if(args.length == 0) {
							Class<?> target = method.getReturnType();
							try {
								Object response = method.invoke(o);
								if(response != null) {
									String value = response.toString();
									String name = lowerCaseStart(field);
									result.add(new NameValue(name, value));
									log.info("Added field "+name+" of type "+response.getClass().getName()+" with value "+value);
								}
							} catch(Exception e) {
								log.log(Level.WARNING,method.getName()+" with return "+target.getCanonicalName(), e);
							}
							
						}
					}
				}
			}
		}
		return result;
	}
	private static String lowerCaseStart(String s) {
		String result = s.substring(0,1).toLowerCase();
		if(s.length() > 1)
			result += s.substring(1);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private static String getPrivateAddresses(Object response){
		List<String> list = new ArrayList<String>();
		
		Multimap<String,Address> multimap = (Multimap<String,Address>)response;
		for (String access : multimap.keySet()) {
			if(access.compareTo("private")==0){
				Collection<Address> addresses = multimap.get(access);
				for (Iterator iter = addresses.iterator(); iter.hasNext();) {
					   Address add = (Address) iter.next();
					   list.add(add.getAddr());
					}
			}
		}
		
		return list.toString();
	}
	
	@SuppressWarnings("unchecked")
	private static String getPublicAddresses(Object response){
		List<String> list = new ArrayList<String>();
				
		Multimap<String,Address> multimap = (Multimap<String,Address>)response;
		for (String access : multimap.keySet()) {
			if(access.compareTo("public")==0){
				Collection<Address> addresses = multimap.get(access);
				for (Iterator iter = addresses.iterator(); iter.hasNext();) {
					   Address add = (Address) iter.next();
					   list.add(add.getAddr());
					}
			}
		}
		
		return list.toString();
	}
	
	private static Long getPowerState(Object response){
		ServerExtendedStatus status = (ServerExtendedStatus)response;
		int powerState = status.getPowerState();
		Long ret = new Long(powerState);
		return ret;
	}
	
	private static String getTaskState(Object response){
		ServerExtendedStatus status = (ServerExtendedStatus)response;
		
		return status.getTaskState();
	}
	
	private static String getVMState(Object response){
		ServerExtendedStatus status = (ServerExtendedStatus)response;
		
		return status.getVmState();
	}
	
	private static String getInstanceName(Object response){
		ServerExtendedAttributes attributes = (ServerExtendedAttributes) response;
		
		return attributes.getInstanceName();
	}
	
	private static String getHostName(Object response){
		ServerExtendedAttributes attributes = (ServerExtendedAttributes) response;
		
		return attributes.getHostName();
	}
	
	private static String getHypervisorHostName(Object response){
		ServerExtendedAttributes attributes = (ServerExtendedAttributes) response;
		
		return attributes.getHypervisorHostName();
	}
	
	
}
