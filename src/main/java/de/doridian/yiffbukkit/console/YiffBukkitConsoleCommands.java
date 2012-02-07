package de.doridian.yiffbukkit.console;

import de.doridian.yiffbukkit.YiffBukkit;

public class YiffBukkitConsoleCommands {
	public YiffBukkitConsoleCommands(YiffBukkit plugin) {
		plugin.getCommand("yb").setExecutor(new YiffBukkitCommandExecutor(plugin));
	}
}
