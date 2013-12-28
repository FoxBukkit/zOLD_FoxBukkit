package de.doridian.yiffbukkit.main.console;

import de.doridian.yiffbukkit.core.YiffBukkit;

public class YiffBukkitConsoleCommands {
	public YiffBukkitConsoleCommands(YiffBukkit plugin) {
		plugin.getCommand("yb").setExecutor(new YiffBukkitCommandExecutor(plugin));
	}
}
