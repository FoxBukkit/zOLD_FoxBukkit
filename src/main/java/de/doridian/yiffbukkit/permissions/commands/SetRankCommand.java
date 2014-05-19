package de.doridian.yiffbukkit.permissions.commands;

import de.doridian.yiffbukkit.bans.FishBansResolver;
import de.doridian.yiffbukkit.core.util.PlayerHelper;
import de.doridian.yiffbukkit.main.PermissionDeniedException;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.AbusePotential;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;
import de.doridian.yiffbukkit.permissions.YiffBukkitPermissions;
import org.bukkit.command.CommandSender;

import java.util.UUID;

@Names("setrank")
@Help("Sets rank of specified user")
@Usage("<full name> <rank>")
@ICommand.BooleanFlags("p")
@Permission("yiffbukkit.users.setrank")
@AbusePotential
public class SetRankCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr, String commandName) throws YiffBukkitCommandException {
		args = parseFlags(args);

		String otherName = args[0];
		UUID otherUUID = FishBansResolver.getUUID(args[0]);
		String newRank = args[1];
		String oldRank = PlayerHelper.getPlayerRank(otherUUID);
		
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

		int selflvl = PlayerHelper.getPlayerLevel(commandSender);
		int oldlvl = PlayerHelper.getPlayerLevel(otherUUID);
		int newlvl = playerHelper.getRankLevel(newRank);

		if(selflvl <= oldlvl)
			throw new PermissionDeniedException();

		if(selflvl <= newlvl)
			throw new PermissionDeniedException();

		int opLvl = playerHelper.getRankLevel("op");

		if(playerHelper.getRankLevel(newRank) >= opLvl && !commandSender.hasPermission("yiffbukkit.users.makestaff"))
			throw new PermissionDeniedException();
		
		if(PlayerHelper.getPlayerLevel(otherUUID) >= opLvl && !commandSender.hasPermission("yiffbukkit.users.modifystaff"))
			throw new PermissionDeniedException();

		if(booleanFlags.contains('p') && newlvl < oldlvl)
			throw new PermissionDeniedException();

		if (PlayerHelper.isGuestRank(newRank)) {
			YiffBukkitPermissions.addCOPlayer(otherName);
		} else {
			YiffBukkitPermissions.removeCOPlayer(otherName);
		}

		playerHelper.setPlayerRank(otherUUID, newRank);

		PlayerHelper.sendServerMessage(commandSender.getName() + " set rank of " + otherName + " to " + newRank);
	}
}
