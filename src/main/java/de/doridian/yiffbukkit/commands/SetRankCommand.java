package de.doridian.yiffbukkit.commands;

import org.bukkit.command.CommandSender;

import de.doridian.yiffbukkit.PermissionDeniedException;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.chat.ChatHelper;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("setrank")
@Help("Sets rank of specified user")
@Usage("<full name> <rank>")
@Permission("yiffbukkit.users.setrank")
public class SetRankCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		requireSSL(commandSender);

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

		if(selflvl <= playerHelper.getPlayerLevel(otherName))
			throw new PermissionDeniedException();

		if(selflvl <= playerHelper.getRankLevel(newRank))
			throw new PermissionDeniedException();
		
		if(playerHelper.getRankLevel(newRank) >= 4 && !plugin.permissionHandler.has(commandSender, "yiffbukkit.users.makestaff"))
			throw new PermissionDeniedException();
		
		if(playerHelper.getPlayerLevel(otherName) >= 4 && !plugin.permissionHandler.has(commandSender, "yiffbukkit.users.modifystaff"))
			throw new PermissionDeniedException();

		playerHelper.setPlayerRank(otherName, newRank);

		playerHelper.sendServerMessage(commandSender.getName() + " set rank of " + otherName + " to " + newRank);
		
		try {
			ChatHelper.getInstance().verifyPlayerInDefaultChannel(plugin.getServer().getPlayerExact(otherName));
		} catch(Exception e) { }
	}
}
