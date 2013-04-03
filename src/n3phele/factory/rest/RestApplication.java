package n3phele.factory.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import n3phele.factory.rest.impl.VirtualServerResource;
/**
 * @author Alexandre Leites
 */
public class RestApplication extends Application {
     public Set<Class<?>> getClasses() {
         Set<Class<?>> s = new HashSet<Class<?>>();
         s.add(VirtualServerResource.class);
         return s;
     }
}