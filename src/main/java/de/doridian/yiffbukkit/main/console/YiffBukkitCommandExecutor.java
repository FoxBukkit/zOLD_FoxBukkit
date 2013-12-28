package de.doridian.yiffbukkit.main.console;

import de.doridian.yiffbukkit.core.YiffBukkit;
import de.doridian.yiffbukkit.main.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class YiffBukkitCommandExecutor implements CommandExecutor {
	private YiffBukkit plugin;

	public YiffBukkitCommandExecutor(YiffBukkit plug) {
		plugin = plug;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) return true;
		plugin.commandSystem.runCommand(sender, Utils.concatArray(args, 0, ""));
		return true;
	}
}
