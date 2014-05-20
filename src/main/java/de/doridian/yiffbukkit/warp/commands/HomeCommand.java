/**
 * This file is part of YiffBukkit.
 *
 * YiffBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * YiffBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with YiffBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.doridian.yiffbukkit.warp.commands;

import de.doridian.yiffbukkit.core.util.PlayerHelper;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.util.Utils;
import org.bukkit.entity.Player;

import java.util.Set;

@Names("home")
@ICommand.Usage("[-l] [name]")
@Help("Teleports you to your home position (see /sethome)")
@ICommand.BooleanFlags("l")
@Permission("yiffbukkit.teleport.basic.home")
public class HomeCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr, String commandName) throws YiffBukkitCommandException {
		args = parseFlags(args);

		if (playerHelper.isPlayerJailed(ply)) {
			PlayerHelper.sendDirectedMessage(ply, "You are jailed!");
			return;
		}

		String homeName = "default";
		if(args.length > 0) {
			homeName = args[0];
		}

		if(booleanFlags.contains('l')) {
			Set<String> homeNames = playerHelper.getPlayerHomePositionNames(ply);
			PlayerHelper.sendDirectedMessage(ply, "Home locations [" + ((homeNames.size() > 0) ? (homeNames.size() - 1) : 0) + "/" + playerHelper.getPlayerHomePositionLimit(ply.getUniqueId()) + "]: " + Utils.concat(homeNames, 0, ""));
			return;
		}

		plugin.playerHelper.teleportWithHistory(ply, playerHelper.getPlayerHomePosition(ply, homeName));
		PlayerHelper.sendDirectedMessage(ply, "You went to your home [" + homeName + "]");
	}
}
