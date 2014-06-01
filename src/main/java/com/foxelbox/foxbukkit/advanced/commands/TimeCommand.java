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
package com.foxelbox.foxbukkit.advanced.commands;

import com.foxelbox.foxbukkit.core.util.PlayerHelper;
import com.foxelbox.foxbukkit.core.util.PlayerHelper.WeatherType;
import com.foxelbox.foxbukkit.main.FoxBukkitCommandException;
import com.foxelbox.foxbukkit.main.PermissionDeniedException;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Help;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Names;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Permission;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.StringFlags;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Usage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Names("time")
@Help("Forces/fixes current time *clientside*.")
@Usage("[normal|night|day|morning|afternoon|<0-23>]")
@StringFlags("ws")
@Permission("foxbukkit.time")
public class TimeCommand extends ServerTimeCommand {
	@Override
	protected void setTime(CommandSender commandSender, Long setTime, Long displayTime, WeatherType setWeather) throws FoxBukkitCommandException {
		if (booleanFlags.contains('s')) {
			if (!commandSender.hasPermission("foxbukkit.time.set"))
				throw new PermissionDeniedException();

			if (setTime == null)
				throw new FoxBukkitCommandException("Must provide a time!");

			if (commandSender instanceof Player) {
				final World world = ((Player) commandSender).getWorld();
				world.setTime(setTime);

				PlayerHelper.sendDirectedMessage(commandSender, "You set the time on '"+world.getName()+"' to: " + displayTime + ":00");
			}
			else {
				for (World world : Bukkit.getWorlds()) {
					world.setTime(setTime);
				}

				PlayerHelper.sendDirectedMessage(commandSender, "You set the time on all worlds to: " + displayTime + ":00");
			}


			return;
		}
		if (setTime == null) {
			playerHelper.resetFrozenServerTime(asPlayer(commandSender));
			PlayerHelper.sendDirectedMessage(commandSender, "Reset your time back to normal!");
		}
		else {
			playerHelper.setFrozenServerTime(asPlayer(commandSender), setTime);
			PlayerHelper.sendDirectedMessage(commandSender, "You forced your time to be: " + displayTime + ":00");
		}

		if (setWeather == null) {
			playerHelper.frozenWeathers.remove(commandSender.getUniqueId());
			PlayerHelper.sendDirectedMessage(commandSender, "Reset your weather back to normal!");
		}
		else {
			playerHelper.frozenWeathers.put(commandSender.getUniqueId(), setWeather);
			PlayerHelper.sendDirectedMessage(commandSender, "You forced your weather to be: " + setWeather.name + ".");
		}

		playerHelper.pushWeather(asPlayer(commandSender));
	}
}
