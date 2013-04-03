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
 
 package n3phele.factory.model;

import java.net.URI;

import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import n3phele.service.model.core.Collection;
import n3phele.service.model.core.Entity;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Unindexed;
/**
 * @author Nigel Cook
 */
@XmlRootElement(name="VirtualServers")
@XmlType(name="VirtualServers", propOrder={})
@Unindexed
@Cached
public class VirtualServers extends Collection<Entity> {
	private static final long serialVersionUID = -3896104069780190325L;
	
	@Id private String id;
	
	public VirtualServers() {}
	
	public VirtualServers(String name, URI uri) {
		super(name, uri);
		id = name;
	}

	/**
	 * @return the id
	 */
	@XmlTransient
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	

}
