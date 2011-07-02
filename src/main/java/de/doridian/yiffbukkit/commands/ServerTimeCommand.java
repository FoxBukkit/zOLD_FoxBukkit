package de.doridian.yiffbukkit.commands;

import java.util.Hashtable;

import org.bukkit.command.CommandSender;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;
import de.doridian.yiffbukkit.util.PlayerHelper.WeatherType;

@Names("servertime")
@Help("Forces/fixes current time *serverside*.")
@Usage("[normal|night|day|morning|afternoon|<0-23>]")
@Level(3)
@StringFlags("w")
@Permission("yiffbukkit.servertime")
public class ServerTimeCommand extends ICommand {
	private static final Hashtable<String,Long> timeSwatches = new Hashtable<String,Long>();
	static {
		timeSwatches.put("night", 0L);
		timeSwatches.put("morning", 6L);
		timeSwatches.put("day", 12L);
		timeSwatches.put("afternoon", 18L);
	};

	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		args = parseFlags(args);

		String weather = stringFlags.get('w');
		final WeatherType weatherType;
		if (weather == null)
			weatherType = null;
		else if (weather.equalsIgnoreCase("rain"))
			weatherType = WeatherType.RAIN;
		else if (weather.equalsIgnoreCase("thunderstorm"))
			weatherType = WeatherType.THUNDERSTORM;
		else if (weather.equalsIgnoreCase("thunder"))
			weatherType = WeatherType.THUNDERSTORM;
		else if (weather.equalsIgnoreCase("none"))
			weatherType = WeatherType.CLEAR;
		else if (weather.equalsIgnoreCase("clear"))
			weatherType = WeatherType.CLEAR;
		else
			throw new YiffBukkitCommandException("Invalid weather specified.");

		long displayTime;

		if (args.length == 0 || args[0].equalsIgnoreCase("normal")) {
			setTime(commandSender, null, null, weatherType);
			return;
		}
		else if (timeSwatches.containsKey(args[0].toLowerCase())) {
			displayTime = timeSwatches.get(args[0].toLowerCase());
		}
		else {
			try
			{
				displayTime = Long.valueOf(args[0]);
			}
			catch (Exception e) {
				throw new YiffBukkitCommandException("Usage: " + getUsage(), e);
			}
		}

		final long setTime = ((displayTime+18)%24)*1000;
		setTime(commandSender, setTime, displayTime, weatherType);
	}

	protected void setTime(CommandSender commandSender, Long setTime, Long displayTime, WeatherType setWeather) throws YiffBukkitCommandException {
		playerHelper.frozenServerTime = setTime;
		if (setTime == null) {
			playerHelper.sendServerMessage(commandSender.getName() + " reset the server time back to normal!");
		}
		else {
			playerHelper.sendServerMessage(commandSender.getName() + " forced the server time to be: " + displayTime + ":00");
		}

		playerHelper.frozenServerWeather = setWeather;
		if (setWeather == null) {
			playerHelper.sendServerMessage(commandSender.getName() + " reset the server weather back to normal!");
		}
		else {
			playerHelper.sendServerMessage(commandSender.getName() + " forced the server weather to be: " + setWeather.name + ".");
		}

		playerHelper.pushWeather();
	}
}
