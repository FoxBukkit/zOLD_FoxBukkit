package de.doridian.yiffbukkit.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class Configuration {
	private static HashMap<String,String> configValues = new HashMap<String,String>();
	static {
		configValues.clear();
		try {
			BufferedReader stream = new BufferedReader(new FileReader("yiffbukkit-config.txt"));
			String line; int lpos;
			while((line = stream.readLine()) != null) {
				lpos = line.lastIndexOf('=');
				configValues.put(line.substring(0,lpos), line.substring(lpos+1));
			}
			stream.close();
		}
		catch (Exception e) { }
	}
	public static String getValue(String key, String def) {
		if(configValues.containsKey(key)) {
			return configValues.get(key);
		}
		return def;
	}
}
