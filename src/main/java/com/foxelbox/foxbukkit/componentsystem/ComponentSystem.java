/**
 * This file is part of FoxBukkit.
 *
 * FoxBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoxBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FoxBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.foxelbox.foxbukkit.componentsystem;

import com.foxelbox.foxbukkit.jail.JailComponent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ComponentSystem {
	private final Map<String, Component> loadedComponents = new LinkedHashMap<>();

	public void registerComponents() {
		loadedComponents.put("advanced", new com.foxelbox.foxbukkit.advanced.Main());
		loadedComponents.put("fun", new com.foxelbox.foxbukkit.fun.Main());
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
