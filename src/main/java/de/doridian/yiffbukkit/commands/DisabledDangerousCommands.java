package de.doridian.yiffbukkit.commands;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import org.bukkit.command.CommandSender;

@ICommand.Names({"op", "deop", "stop", "save-on", "save-all", "save-off", "say"})
@ICommand.Help("Commands that are not supposed to be used, at all")
@ICommand.Level(0)
public class DisabledDangerousCommands extends ICommand {
	public void run(final CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		throw new YiffBukkitCommandException("Sorry, this command can only be run using /rcon or from local console!");
	}
}
