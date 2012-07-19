package de.doridian.yiffbukkit.warp.commands;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.ICommand;
import org.bukkit.command.CommandSender;

@ICommand.Names("sethomelimit")
@ICommand.Usage("a")
@ICommand.Help("Sets home location limit for a player")
@ICommand.Permission("yiffbukkit.teleport.admin.sethomelocationlimit")
public class SetHomeLocationLimit extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		args = parseFlags(args);
		int newLimit = Integer.parseInt(args[1]);
		if(booleanFlags.contains('a')) {
			newLimit += playerHelper.getPlayerHomePositionLimit(args[0]);
		}
		playerHelper.setPlayerHomePositionLimit(args[0], newLimit);
	}
}
