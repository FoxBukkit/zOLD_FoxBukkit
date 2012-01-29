package de.doridian.yiffbukkit.console;

import de.doridian.yiffbukkit.YiffBukkit;

public class YiffBukkitConsoleCommands {
	public YiffBukkitConsoleCommands(YiffBukkit plugin) {
		plugin.getCommand("rawlist").setExecutor(new RawListExecutor());
		plugin.getCommand("yb").setExecutor(new YiffBukkitCommandExecutor(plugin));
	}
}
