package de.doridian.yiffbukkit.componentsystem;

import de.doridian.yiffbukkit.jail.JailComponent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ComponentSystem {
	Map<String, Component> loadedComponents = new LinkedHashMap<String, Component>();

	public void registerComponents() {
		loadedComponents.put("advanced", new de.doridian.yiffbukkit.advanced.Main());
		loadedComponents.put("fun", new de.doridian.yiffbukkit.fun.Main());
		loadedComponents.put("jail", new JailComponent());
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

	public Component getComponent(String name) {
		final Component component = loadedComponents.get(name);
		if (component == null)
			throw new RuntimeException("Tried to get component that wasn't loaded.");

		return component;
	}
}
