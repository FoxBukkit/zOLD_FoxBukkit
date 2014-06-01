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
import com.foxelbox.foxbukkit.main.commands.system.ICommand;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Help;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Names;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Permission;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.StringFlags;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Usage;
import org.bukkit.command.CommandSender;

import java.util.Hashtable;

@Names("servertime")
@Help("Forces/fixes current time *serverside*.")
@Usage("[normal|night|day|morning|afternoon|<0-23>]")
@StringFlags("w")
@Permission("foxbukkit.servertime")
public class ServerTimeCommand extends ICommand {
	private static final Hashtable<String,Long> timeSwatches = new Hashtable<>();
	static {
		timeSwatches.put("night", 0L);
		timeSwatches.put("morning", 6L);
		timeSwatches.put("day", 12L);
		timeSwatches.put("afternoon", 18L);
	}

	@Override
	public void run(CommandSender commandSender, String[] args, String argStr, String commandName) throws FoxBukkitCommandException {
		args = parseFlags(args);

		final String weather = stringFlags.get('w');
		final WeatherType weatherType = getWeatherType(weather);

		long displayTime;

		if (args.length == 0 || args[0].equalsIgnoreCase("normal")) {
			setTime(commandSender, null, null, weatherType);
			return;
		}
		else if (timeSwatches.containsKey(args[0].toLowerCase())) {
			displayTime = timeSwatches.get(args[0].toLowerCase());
		}
		else {
			try {
				displayTime = Long.valueOf(args[0]);
			}
			catch (Exception e) {
				throw new FoxBukkitCommandException("Usage: " + getUsage(), e);
			}
		}

		final long setTime = ((displayTime+18)%24)*1000;
		setTime(commandSender, setTime, displayTime, weatherType);
	}

	private WeatherType getWeatherType(String weather) throws FoxBukkitCommandException {
		if (weather == null) {
			return null;
		}

		switch (weather.toLowerCase()) {
		case "rain":
			return WeatherType.RAIN;

		case "thunderstorm":
		case "thunder":
			return WeatherType.THUNDERSTORM;

		case "none":
		case "clear":
			return WeatherType.CLEAR;

		default:
			throw new FoxBukkitCommandException("Invalid weather specified.");
		}
	}

	protected void setTime(CommandSender commandSender, Long setTime, Long displayTime, WeatherType setWeather) throws FoxBukkitCommandException {
		if (setTime == null) {
			playerHelper.resetFrozenServerTime();
			PlayerHelper.sendServerMessage(commandSender.getName() + " reset the server time back to normal!");
		}
		else {
			playerHelper.setFrozenServerTime(setTime);
			PlayerHelper.sendServerMessage(commandSender.getName() + " forced the server time to be: " + displayTime + ":00");
		}

		playerHelper.frozenServerWeather = setWeather;
		if (setWeather == null) {
			PlayerHelper.sendServerMessage(commandSender.getName() + " reset the server weather back to normal!");
		}
		else {
			PlayerHelper.sendServerMessage(commandSender.getName() + " forced the server weather to be: " + setWeather.name + ".");
		}

		playerHelper.pushWeather();
	}
}
