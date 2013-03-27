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


import n3phele.service.model.core.NameValue;

import org.jclouds.openstack.nova.v2_0.domain.Address;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Multimap;

/** Introspects an object extracting name/value pairs.
 * @author Nigel Cook
 *
 */
public class HPCloudExtractor {
	final static Logger logger = LoggerFactory.getLogger(HPCloudExtractor.class);

	/** Extract object name/value pairs by introspection. The processing looks for object methods that being with "get" and
	 * have no arguments. The methods are invoked and if a non-null result is returned, then the method name with "get" removed
	 * and the string version of the result form a name/value pair.
	 * @param o object to be introspected
	 * @return list of extracted name/value pairs
	 */
	public static ArrayList<NameValue> extract(Server o) {
		//logger.info("Extracting output parameters");
		ArrayList<NameValue> result = new ArrayList<NameValue>();
		Method methods[] = o.getClass().getMethods();
		for(Method method : methods) {
			if(method.getName().startsWith("get")) {
				String field = method.getName().substring("get".length());
				//Ignore map of metadata
				if((field.compareTo("Metadata"))!=0 && (field.compareTo("Status")!=0) && (field.compareTo("ExtendedStatus")!=0) && (field.compareTo("ExtendedAttributes")!=0)){
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
									result.add(new NameValue("privateIpAddress", valuePrivate));
									result.add(new NameValue("publicIpAddress", valuePublic));
									//logger.info("Added field privateIpAddress of type "+List.class+" with value "+valuePrivate);
									//logger.info("Added field publicIpAddress of type "+List.class+" with value "+valuePublic);
								}
							} catch(Exception e) {
								logger.warn(method.getName()+" with return "+target.getCanonicalName(), e);
							}
						
						}
					}			
					else{
						
						Class<?> args[] = method.getParameterTypes();
						if(args.length == 0) {
							Class<?> target = method.getReturnType();
							try {
								Object response = method.invoke(o);
								if(response != null) {
									String value = response.toString();
									String name = lowerCaseStart(field);
									result.add(new NameValue(name, value));
									//logger.info("Added field "+name+" of type "+response.getClass().getName()+" with value "+value);
								}
							} catch(Exception e) {
								logger.warn(method.getName()+" with return "+target.getCanonicalName(), e);
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
		String address = "";
		
		Multimap<String,Address> multimap = (Multimap<String,Address>)response;
		for (String access : multimap.keySet()) {
			if(access.compareTo("private")==0){
				Collection<Address> addresses = multimap.get(access);
				for (Iterator iter = addresses.iterator(); iter.hasNext();) {
					   Address add = (Address) iter.next();
					   address = (add.getAddr());
					   return address;
					   }
			}
		}
		
		return address;
	}
	
	@SuppressWarnings("unchecked")
	private static String getPublicAddresses(Object response){
		String address = "";
				
		Multimap<String,Address> multimap = (Multimap<String,Address>)response;
		for (String access : multimap.keySet()) {
			//logger.info("multimap key access:" + access);
				Collection<Address> addresses = multimap.get(access);
				//logger.info("Collection size:" + addresses.size());
				for (Iterator iter = addresses.iterator(); iter.hasNext();) {
					   Address add = (Address) iter.next();
					   address = (add.getAddr());
					  
					   }
		}
		
		return address;
	}
	
}
