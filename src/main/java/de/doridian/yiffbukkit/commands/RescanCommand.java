package de.doridian.yiffbukkit.commands;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.Help;
import de.doridian.yiffbukkit.commands.ICommand.Names;
import de.doridian.yiffbukkit.commands.ICommand.Permission;
import org.bukkit.command.CommandSender;

@Names("rescan")
@Help("Rescans YiffBukkit's commands. Pretty useless without a debug connection.")
@Permission("yiffbukkit.rescancommands")
public class RescanCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr)
	throws YiffBukkitCommandException {
		plugin.playerListener.scanCommands();

		playerHelper.sendDirectedMessage(commandSender, "Rescanned YiffBukkit commands.");
	}
}
