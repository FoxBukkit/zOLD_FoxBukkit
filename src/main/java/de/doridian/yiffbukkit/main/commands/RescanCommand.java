package de.doridian.yiffbukkit.main.commands;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;

import org.bukkit.command.CommandSender;

@Names("rescan")
@Help("Rescans YiffBukkit's commands. Pretty useless without a debug connection.")
@Permission("yiffbukkit.rescancommands")
public class RescanCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		plugin.commandSystem.scanCommands();

		PlayerHelper.sendDirectedMessage(commandSender, "Rescanned YiffBukkit commands.");
	}
}
