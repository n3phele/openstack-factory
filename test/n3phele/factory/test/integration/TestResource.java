package n3phele.factory.test.integration;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class TestResource {
	
	protected ResourceBundle bundle;
	protected Map<String, String> globalSettings = null;
	
	public TestResource(String filePath) throws FileNotFoundException {
		try {
			globalSettings = new HashMap<String, String>();
			bundle = ResourceBundle.getBundle(filePath,
				Locale.getDefault(), this.getClass().getClassLoader());
			for(String i : bundle.keySet()) {
				globalSettings.put(i, bundle.getString(i));
			}
		} catch (Exception e) {
			throw new FileNotFoundException();
		}
	}
	public String get(String key, String defaultValue) {
		String result = defaultValue;
		
		try {
			if(globalSettings != null && globalSettings.containsKey(key))
				result = globalSettings.get(key);
			else
				result = defaultValue;
		} catch (Exception e) {
			result = defaultValue;
		}
		return result;
	}
	
	public boolean get(String key, boolean defaultValue) {
		boolean result = defaultValue;
		
		try {
			if(globalSettings.containsKey(key))
				result = Boolean.valueOf(globalSettings.get(key));
			else
				result = defaultValue;
		} catch (Exception e) {
			result = defaultValue;
		} 
		return result;
	}
	
}