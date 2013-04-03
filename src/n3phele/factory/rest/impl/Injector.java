/**
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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Injects name/value pairs into an object.
 * @author Nigel Cook
 *
 */
public class Injector {
	private static Logger log = Logger.getLogger(Injector.class.getName());  
	/** Injects name/value pairs from a map into a supplied object by iterating the map, and for each extracted name/value
	 * pair searching for a method with signature set<I>name</I> and one argument, or signature with<I>name</I> and a
	 * String[] argument. The value is transformed to the method argument type from simple objects or by creating an instance
	 * of the argument with a string argument of the value, or using a static valueOf method with the string value as an argument.
	 * @param o object to be injected with values
	 * @param params name/value pairs to be injected
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void inject(Object o, Map<String,String>params) {
		Method methods[] = o.getClass().getMethods();
		for(Method method : methods) {
			if(method.getName().startsWith("set")) {
				String field = method.getName().substring("set".length());
				String argName = lowerCaseStart(field);
				if(params.containsKey(argName)) {
					String argValue = params.get(argName);
					
					// FIXME
					// not sure if apache BeanUtils would be better at this.
					Class args[] = method.getParameterTypes();
					if(args.length == 1) {
						Class target = args[0];
						if(target.isAssignableFrom(String.class)) {
							try {
								log.info(method.getName()+" with String "+argValue);
								method.invoke(o, argValue);
							} catch (Exception e) {
								log.log(Level.WARNING,method.getName()+" with String "+argValue, e);
							} 
							continue;
						}  else if(target.isAssignableFrom(long.class)) {
							try {
								log.info(method.getName()+" with long "+argValue);
								method.invoke(o, Long.valueOf(argValue));
							} catch (Exception e) {
								log.log(Level.WARNING,method.getName()+" with long "+argValue, e);
							} 
							continue;
						} else if(target.isAssignableFrom(int.class)) {
							try {
								log.info(method.getName()+" with int "+argValue);
								method.invoke(o, Integer.valueOf(argValue));
							} catch (Exception e) {
								log.log(Level.WARNING,method.getName()+" with int "+argValue, e);
							} 
							continue;
						}  else if(target.isAssignableFrom(double.class)) {
							try {
								log.info(method.getName()+" with double "+argValue);
								method.invoke(o, Double.valueOf(argValue));
							} catch (Exception e) {
								log.log(Level.WARNING,method.getName()+" with double "+argValue, e);
							} 
							continue;
						} else if(target.isAssignableFrom(float.class)) {
							try {
								log.info(method.getName()+" with float "+argValue);
								method.invoke(o, Float.valueOf(argValue));
							} catch (Exception e) {
								log.log(Level.WARNING,method.getName()+" with float "+argValue, e);
							} 
							continue;
						} else {
							try {
								Constructor constructor = target.getConstructor(String.class);
								try {
									log.info(method.getName()+" with constructor "+target.getName()+" value "+argValue);
									Object fieldObj = constructor.newInstance(argValue);
									method.invoke(o, fieldObj);
								} catch (Exception e) {
									log.log(Level.WARNING, method.getName()+" with constructor "+target.getName()+" value "+argValue, e);
								}
								continue;
							} catch (Exception e) {
							}
							try {
								Method alternate = target.getMethod("valueOf", String.class);
								try {
									log.info(method.getName()+" with valueOf "+target.getName()+" value "+argValue);
									Object fieldObj = alternate.invoke(argValue);
									method.invoke(o, fieldObj);
								} catch (Exception e) {
									log.log(Level.WARNING, method.getName()+" with constructor "+target.getName()+" value "+argValue, e);
								}
								continue;
							} catch (Exception e) {
							}
							try {
								Method with = o.getClass().getMethod("with"+field, String[].class);
								try {
									log.info(with.getName()+" with String value "+argValue);
									o = with.invoke(o, new Object[]{new String[] {argValue}});
								} catch (Exception e) {
									log.log(Level.WARNING, with.getName()+" with String value "+argValue, e);
								}
								continue;
							} catch (Exception e) {
								
							} 
							log.warning("Unable to "+ method.getName()+ " with parameter "+ target.getName());
						}
					}
				}
			}
		}
	}
	private static String lowerCaseStart(String s) {
		String result = s.substring(0,1).toLowerCase();
		if(s.length() > 1)
			result += s.substring(1);
		return result;
	}
}
