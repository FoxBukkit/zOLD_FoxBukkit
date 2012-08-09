package de.doridian.yiffbukkit.permissions.commands;

import de.doridian.yiffbukkit.main.PermissionDeniedException;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.ICommand;
import de.doridian.yiffbukkit.chat.ChatHelper;
import de.doridian.yiffbukkit.main.commands.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.ICommand.Usage;
import de.doridian.yiffbukkit.permissions.YiffBukkitPermissions;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;

import org.bukkit.command.CommandSender;

@Names("setrank")
@Help("Sets rank of specified user")
@Usage("<full name> <rank>")
@ICommand.BooleanFlags("p")
@Permission("yiffbukkit.users.setrank")
public class SetRankCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		requireSSL(commandSender);

		args = parseFlags(args);

		String otherName = args[0];
		String newRank = args[1];
		String oldRank = playerHelper.getPlayerRank(otherName);
		
		if(oldRank.equalsIgnoreCase("banned")) {
			throw new YiffBukkitCommandException("Player is banned! /unban first!");
		}
		
		if(newRank.equalsIgnoreCase("banned")) {
			throw new YiffBukkitCommandException("Please use /ban to ban people!");
		}

		if (newRank.equalsIgnoreCase(oldRank))
			throw new YiffBukkitCommandException("Player already has that rank!");

		if(!playerHelper.ranklevels.containsKey(newRank)) {
			throw new YiffBukkitCommandException("Rank does not exist!");
		}

		int selflvl = playerHelper.getPlayerLevel(commandSender);
		int oldlvl = playerHelper.getPlayerLevel(otherName);
		int newlvl = playerHelper.getRankLevel(newRank);

		if(selflvl <= oldlvl)
			throw new PermissionDeniedException();

		if(selflvl <= newlvl)
			throw new PermissionDeniedException();
		
		if(playerHelper.getRankLevel(newRank) >= 4 && !commandSender.hasPermission("yiffbukkit.users.makestaff"))
			throw new PermissionDeniedException();
		
		if(playerHelper.getPlayerLevel(otherName) >= 4 && !commandSender.hasPermission("yiffbukkit.users.modifystaff"))
			throw new PermissionDeniedException();

		if(booleanFlags.contains('p') && newlvl < oldlvl)
			throw new PermissionDeniedException();

		if (PlayerHelper.isGuestRank(newRank)) {
			YiffBukkitPermissions.addCOPlayer(otherName);
		} else {
			YiffBukkitPermissions.removeCOPlayer(otherName);
		}

		playerHelper.setPlayerRank(otherName, newRank);

		playerHelper.sendServerMessage(commandSender.getName() + " set rank of " + otherName + " to " + newRank);
		
		try {
			ChatHelper.getInstance().verifyPlayerInDefaultChannel(plugin.getServer().getPlayerExact(otherName));
		} catch(Exception e) { }
	}
}
