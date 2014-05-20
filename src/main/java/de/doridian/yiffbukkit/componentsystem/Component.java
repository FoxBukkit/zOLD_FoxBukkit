/**
 * This file is part of YiffBukkit.
 *
 * YiffBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * YiffBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with YiffBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.doridian.yiffbukkit.componentsystem;

import de.doridian.yiffbukkit.core.YiffBukkit;
import de.doridian.yiffbukkit.core.util.PlayerHelper;
import de.doridian.yiffbukkit.main.util.Utils;

import java.lang.reflect.InvocationTargetException;

public abstract class Component {
	protected final YiffBukkit plugin;
	protected final PlayerHelper playerHelper;

	public Component() {
		plugin = YiffBukkit.instance;
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
		for (Class<?> cls : Utils.getSubClasses(YBListener.class, packageName+".listeners")) {
			try {
				cls.getConstructor(YiffBukkit.class).newInstance(plugin);
				System.out.println("Registered Listener '"+cls.getName()+"' for component '"+packageName+"'.");
				continue;
			}
			catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException ignored) { }

			try {
				cls.newInstance();
				System.out.println("Registered Listener '"+cls.getName()+"' for component '"+packageName+"'.");
				continue;
			} catch (InstantiationException | IllegalAccessException e) {
				continue;
			}
		}
	}

	public void onEnable() {
		registerCommands();
		registerListeners();
	}
}
