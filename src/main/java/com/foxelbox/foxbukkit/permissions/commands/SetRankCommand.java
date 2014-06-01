/**
 * This file is part of FoxBukkit.
 *
 * FoxBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoxBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FoxBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.foxelbox.foxbukkit.permissions.commands;

import com.foxelbox.foxbukkit.bans.FishBansResolver;
import com.foxelbox.foxbukkit.core.util.PlayerHelper;
import com.foxelbox.foxbukkit.main.FoxBukkitCommandException;
import com.foxelbox.foxbukkit.main.PermissionDeniedException;
import com.foxelbox.foxbukkit.main.commands.system.ICommand;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.AbusePotential;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Help;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Names;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Permission;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Usage;
import com.foxelbox.foxbukkit.permissions.FoxBukkitPermissions;
import org.bukkit.command.CommandSender;

import java.util.UUID;

@Names("setrank")
@Help("Sets rank of specified user")
@Usage("<full name> <rank>")
@ICommand.BooleanFlags("p")
@Permission("foxbukkit.users.setrank")
@AbusePotential
public class SetRankCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr, String commandName) throws FoxBukkitCommandException {
		args = parseFlags(args);

		String otherName = args[0];
		UUID otherUUID = FishBansResolver.getUUID(args[0]);
		String newRank = args[1];
		String oldRank = PlayerHelper.getPlayerRank(otherUUID);
		
		if(oldRank.equalsIgnoreCase("banned")) {
			throw new FoxBukkitCommandException("Player is banned! /unban first!");
		}
		
		if(newRank.equalsIgnoreCase("banned")) {
			throw new FoxBukkitCommandException("Please use /ban to ban people!");
		}

		if (newRank.equalsIgnoreCase(oldRank))
			throw new FoxBukkitCommandException("Player already has that rank!");

		if(!playerHelper.ranklevels.containsKey(newRank)) {
			throw new FoxBukkitCommandException("Rank does not exist!");
		}

		int selflvl = PlayerHelper.getPlayerLevel(commandSender);
		int oldlvl = PlayerHelper.getPlayerLevel(otherUUID);
		int newlvl = playerHelper.getRankLevel(newRank);

		if(selflvl <= oldlvl)
			throw new PermissionDeniedException();

		if(selflvl <= newlvl)
			throw new PermissionDeniedException();

		int opLvl = playerHelper.getRankLevel("op");

		if(playerHelper.getRankLevel(newRank) >= opLvl && !commandSender.hasPermission("foxbukkit.users.makestaff"))
			throw new PermissionDeniedException();
		
		if(PlayerHelper.getPlayerLevel(otherUUID) >= opLvl && !commandSender.hasPermission("foxbukkit.users.modifystaff"))
			throw new PermissionDeniedException();

		if(booleanFlags.contains('p') && newlvl < oldlvl)
			throw new PermissionDeniedException();

		if (PlayerHelper.isGuestRank(newRank)) {
			FoxBukkitPermissions.addCOPlayer(otherName);
		} else {
			FoxBukkitPermissions.removeCOPlayer(otherName);
		}

		playerHelper.setPlayerRank(otherUUID, newRank);

		PlayerHelper.sendServerMessage(commandSender.getName() + " set rank of " + otherName + " to " + newRank);
	}
}
