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
import com.foxelbox.foxbukkit.main.commands.system.ICommand;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Help;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Names;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Permission;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Usage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Names("kickall")
@Help("Kicks everyone from the server except for yourself.")
@Usage("[<reason>]")
@Permission("foxbukkit.users.kickall")
public class KickAllCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr, String commandName) {
		if (argStr.isEmpty())
			argStr = "Clearing server.";

		for (Player player : plugin.getServer().getOnlinePlayers()) {
			if (player.equals(commandSender))
				continue;

			KickCommand.kickPlayer(player, argStr);
		}

		PlayerHelper.sendServerMessage(commandSender.getName() + " kicked everyone (reason: " + argStr + ")");
	}
}
