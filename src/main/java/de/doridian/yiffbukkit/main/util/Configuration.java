package de.doridian.yiffbukkit.main.util;

import de.doridian.yiffbukkit.main.config.ConfigFileReader;

import java.io.BufferedReader;
import java.util.HashMap;

public class Configuration {
	private final static HashMap<String,String> configValues;
	static {
		configValues = new HashMap<>();
		configValues.clear();
		try {
			BufferedReader stream = new BufferedReader(new ConfigFileReader("config.txt"));
			String line; int lpos;
			while((line = stream.readLine()) != null) {
				lpos = line.lastIndexOf('=');
				configValues.put(line.substring(0,lpos), line.substring(lpos+1));
			}
			stream.close();
		}
		catch (Exception e) { e.printStackTrace(); }
	}
	public static String getValue(String key, String def) {
		if(configValues.containsKey(key)) {
			return configValues.get(key);
		}
		return def;
	}
}
