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
package com.foxelbox.foxbukkit.bans.commands;

import com.foxelbox.foxbukkit.core.util.PlayerHelper;
import com.foxelbox.foxbukkit.main.PermissionDeniedException;
import com.foxelbox.foxbukkit.main.commands.system.ICommand;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Help;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Names;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Permission;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Usage;
import com.foxelbox.foxbukkit.main.util.PlayerFindException;
import com.foxelbox.foxbukkit.main.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Names("kick")
@Help("Kicks specified user")
@Usage("<name> [reason here]")
@Permission("foxbukkit.users.kick")
public class KickCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr, String commandName) throws PlayerFindException, PermissionDeniedException {
		final Player otherply = playerHelper.matchPlayerSingle(args[0]);

		if (PlayerHelper.getPlayerLevel(commandSender) < PlayerHelper.getPlayerLevel(otherply))
			throw new PermissionDeniedException();

		final String reason = commandSender.getName() + ": " + Utils.concatArray(args, 1, "Kicked");

		kickPlayer(otherply, reason);
		//playerHelper.SendServerMessage(ply.getName() + " kicked " + otherply.getName() + " (reason: "+reason+")");
	}

	public static void kickPlayer(Player otherply, String reason) {
		otherply.kickPlayer("[Kicked] " + reason);
	}
}
