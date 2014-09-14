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

import com.foxelbox.foxbukkit.core.FoxBukkit;
import com.foxelbox.foxbukkit.core.util.PlayerHelper;
import com.foxelbox.foxbukkit.main.util.Utils;

import java.lang.reflect.InvocationTargetException;

public abstract class Component {
	protected final FoxBukkit plugin;
	protected final PlayerHelper playerHelper;

	public Component() {
		plugin = FoxBukkit.instance;
		playerHelper = plugin.playerHelper;
		System.out.println(getClass());
	}

	public void registerCommands() {
		final String packageName = this.getClass().getPackage().getName();
		plugin.commandSystem.scanCommands(packageName+".commands");
	}

	@SuppressWarnings("UnnecessaryContinue")
	public void registerListeners() {
		final String packageName = this.getClass().getPackage().getName();
		for (Class<?> cls : Utils.getSubClasses(FBListener.class, packageName+".listeners")) {
			try {
				cls.getConstructor(FoxBukkit.class).newInstance(plugin);
				System.out.println("Registered Listener '"+cls.getName()+"' for component '"+packageName+"'.");
				continue;
			}
			catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException ignored) {
			}

			try {
				cls.newInstance();
				System.out.println("Registered Listener '"+cls.getName()+"' for component '"+packageName+"'.");
				continue;
			} catch (InstantiationException | IllegalAccessException ignored) {
			}

			System.err.println("Could not register Listener '"+cls.getName()+"' for component '"+packageName+"'.");
		}
	}

	public void onEnable() {
		registerCommands();
		registerListeners();
	}
}
