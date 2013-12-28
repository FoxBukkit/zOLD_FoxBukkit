package de.doridian.yiffbukkit.advanced.commands;

import de.doridian.yiffbukkit.core.util.PlayerHelper.WeatherType;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.StringFlags;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;
import org.bukkit.command.CommandSender;

import java.util.Hashtable;

@Names("servertime")
@Help("Forces/fixes current time *serverside*.")
@Usage("[normal|night|day|morning|afternoon|<0-23>]")
@StringFlags("w")
@Permission("yiffbukkit.servertime")
public class ServerTimeCommand extends ICommand {
	private static final Hashtable<String,Long> timeSwatches = new Hashtable<>();
	static {
		timeSwatches.put("night", 0L);
		timeSwatches.put("morning", 6L);
		timeSwatches.put("day", 12L);
		timeSwatches.put("afternoon", 18L);
	};

	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		args = parseFlags(args);

		String weather = stringFlags.get('w').toLowerCase();
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
				throw new YiffBukkitCommandException("Usage: " + getUsage(), e);
			}
		}

		final long setTime = ((displayTime+18)%24)*1000;
		setTime(commandSender, setTime, displayTime, weatherType);
	}

	private WeatherType getWeatherType(String weather) throws YiffBukkitCommandException {
		if (weather == null) {
			return null;
		}

		switch (weather) {
		case "rain":
			return WeatherType.RAIN;

		case "thunderstorm":
		case "thunder":
			return WeatherType.THUNDERSTORM;

		case "none":
		case "clear":
			return WeatherType.CLEAR;

		default:
			throw new YiffBukkitCommandException("Invalid weather specified.");
		}
	}

	protected void setTime(CommandSender commandSender, Long setTime, Long displayTime, WeatherType setWeather) throws YiffBukkitCommandException {
		if (setTime == null) {
			playerHelper.resetFrozenServerTime();
			playerHelper.sendServerMessage(commandSender.getName() + " reset the server time back to normal!");
		}
		else {
			playerHelper.setFrozenServerTime(setTime);
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
