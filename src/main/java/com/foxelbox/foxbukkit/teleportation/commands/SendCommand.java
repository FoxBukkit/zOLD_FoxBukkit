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
package com.foxelbox.foxbukkit.teleportation.commands;

import com.foxelbox.foxbukkit.core.util.PlayerHelper;
import com.foxelbox.foxbukkit.main.PermissionDeniedException;
import com.foxelbox.foxbukkit.main.commands.system.ICommand;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Help;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Names;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Permission;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Usage;
import com.foxelbox.foxbukkit.main.util.PlayerFindException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Names("send")
@Help("Teleports the specified source user to the specified target user.")
@Usage("<source> <target>")
@Permission("foxbukkit.teleport.send")
public class SendCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr, String commandName) throws PlayerFindException, PermissionDeniedException {
		Player fromPlayer = playerHelper.matchPlayerSingle(args[0]);

		Player toPlayer = playerHelper.matchPlayerSingle(args[1]);

		if (!playerHelper.canSummon(commandSender, fromPlayer))
			throw new PermissionDeniedException();

		if (!playerHelper.canTp(commandSender, toPlayer))
			throw new PermissionDeniedException();

		plugin.playerHelper.teleportWithHistory(fromPlayer, toPlayer);

		PlayerHelper.sendServerMessage(commandSender.getName() + " sent " + fromPlayer.getName() + " to " + toPlayer.getName());
	}
}
