package de.doridian.yiffbukkit.componentsystem;

import java.lang.reflect.InvocationTargetException;

import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;

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

	public void registerListeners() {
		final String packageName = this.getClass().getPackage().getName();
		for (Class<? extends Object> cls : Utils.getSubClasses(YBListener.class, packageName+".listeners")) {
			try {
				cls.getConstructor(YiffBukkit.class).newInstance(plugin);
				System.out.println("Registered Listener '"+cls.getName()+"' for component '"+packageName+"'.");
				continue;
			}
			catch (NoSuchMethodException e) { }
			catch (InstantiationException e) { }
			catch (IllegalAccessException e) { }
			catch (InvocationTargetException e) { }

			try {
				cls.newInstance();
				System.out.println("Registered Listener '"+cls.getName()+"' for component '"+packageName+"'.");
				continue;
			} catch (InstantiationException e) {
				continue;
			} catch (IllegalAccessException e) {
				continue;
			}
		}
	}

	public void onEnable() {
		registerCommands();
		registerListeners();
	}
}
