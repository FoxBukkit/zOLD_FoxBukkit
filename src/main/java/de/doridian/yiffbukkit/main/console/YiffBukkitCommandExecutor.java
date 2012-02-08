package de.doridian.yiffbukkit.main.console;

import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkitsplit.YiffBukkit;
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
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		if(commandSender instanceof Player) return true;
		plugin.playerListener.runCommand(commandSender, Utils.concatArray(strings, 0, ""));
		return true;
	}
}
