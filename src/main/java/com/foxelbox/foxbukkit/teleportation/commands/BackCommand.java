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
import com.foxelbox.foxbukkit.main.FoxBukkitCommandException;
import com.foxelbox.foxbukkit.main.commands.system.ICommand;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Help;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Names;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Permission;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.StringFlags;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Usage;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.LinkedList;

@Names("back")
@Help("Teleports back specified number of steps")
@Usage("[steps]")
@Permission("foxbukkit.teleport.basic.back")
@StringFlags("t")
public class BackCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr, String commandName) throws FoxBukkitCommandException {
		if(playerHelper.isPlayerJailed(ply)) {
			PlayerHelper.sendDirectedMessage(ply, "You are jailed!");
			return;
		}

		int numSteps = 1;
		if(args.length > 0) {
			numSteps = Integer.parseInt(args[0]);
		}

		LinkedList<Location> teleports = plugin.playerHelper.teleportHistory.get(ply.getUniqueId());
		if(teleports == null) {
			PlayerHelper.sendDirectedMessage(ply, "No teleport history found!");
			return;
		}

		Location goTo = null;
		int curStep = 0;
		for(; curStep < numSteps; curStep++) {
			if(teleports.size() == 0) break;
			goTo = teleports.pollFirst();
		}

		if(goTo == null) {
			PlayerHelper.sendDirectedMessage(ply, "No teleport history found!");
			return;
		}

		ply.teleport(goTo);

		PlayerHelper.sendDirectedMessage(ply, "Teleported back \u00a79"+curStep+"\u00a7f step(s).");
	}
}
