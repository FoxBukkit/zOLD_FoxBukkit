package de.doridian.yiffbukkit.warp.commands;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import org.bukkit.command.CommandSender;

import java.util.UUID;

@ICommand.Names("sethomelimit")
@ICommand.BooleanFlags("a")
@ICommand.Help("Sets home location limit for a player")
@ICommand.Permission("yiffbukkit.teleport.admin.sethomelocationlimit")
public class SetHomeLocationLimit extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr, String commandName) throws YiffBukkitCommandException {
		args = parseFlags(args);
		int newLimit = Integer.parseInt(args[1]);

		UUID playerUUID = UUID.fromString(args[0]);

		if(booleanFlags.contains('a')) {
			newLimit += playerHelper.getPlayerHomePositionLimit(playerUUID);
		}
		playerHelper.setPlayerHomePositionLimit(playerUUID, newLimit);
	}
}
