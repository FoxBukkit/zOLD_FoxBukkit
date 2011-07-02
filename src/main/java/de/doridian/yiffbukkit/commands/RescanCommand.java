package de.doridian.yiffbukkit.commands;

import org.bukkit.command.CommandSender;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("rescan")
@Help("Rescans YiffBukkit's commands. Pretty useless without a debug connection.")
@Level(5)
@Permission("yiffbukkit.rescancommands")
public class RescanCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr)
	throws YiffBukkitCommandException {
		plugin.playerListener.scanCommands();

		playerHelper.sendDirectedMessage(commandSender, "Rescanned YiffBukkit commands.");
	}
}
