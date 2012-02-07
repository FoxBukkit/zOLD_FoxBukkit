package de.doridian.yiffbukkit.main.console;

import de.doridian.yiffbukkitsplit.YiffBukkit;

public class YiffBukkitConsoleCommands {
	public YiffBukkitConsoleCommands(YiffBukkit plugin) {
		plugin.getCommand("yb").setExecutor(new YiffBukkitCommandExecutor(plugin));
	}
}
