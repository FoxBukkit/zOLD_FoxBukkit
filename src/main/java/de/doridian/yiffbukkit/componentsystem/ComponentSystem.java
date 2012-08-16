package de.doridian.yiffbukkit.componentsystem;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ComponentSystem {
	Map<String, Component> loadedComponents = new LinkedHashMap<String, Component>();

	public void registerComponents() {
		loadedComponents.put("fun", new de.doridian.yiffbukkit.fun.Main());
	}

	public void registerCommands() {
		for (Entry<String, Component> entry : loadedComponents.entrySet()) {
			System.out.println("Registering commands for component '"+entry.getKey()+"'.");
			entry.getValue().registerCommands();
		}
	}

	public void registerListeners() {
		for (Entry<String, Component> entry : loadedComponents.entrySet()) {
			System.out.println("Registering listeners for component '"+entry.getKey()+"'.");
			entry.getValue().registerListeners();
		}
	}
}
