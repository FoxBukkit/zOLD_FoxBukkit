package de.doridian.yiffbukkit.componentsystem;

import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;

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
