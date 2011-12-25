package de.doridian.yiffbukkit.commands;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.Help;
import de.doridian.yiffbukkit.commands.ICommand.Names;
import de.doridian.yiffbukkit.commands.ICommand.Permission;
import de.doridian.yiffbukkit.commands.ICommand.StringFlags;
import de.doridian.yiffbukkit.commands.ICommand.Usage;
import de.doridian.yiffbukkit.util.PlayerHelper.WeatherType;
import org.bukkit.command.CommandSender;

@Names("time")
@Help("Forces/fixes current time *clientside*.")
@Usage("[normal|night|day|morning|afternoon|<0-23>]")
@StringFlags("w")
@Permission("yiffbukkit.time")
public class TimeCommand extends ServerTimeCommand {
	@Override
	protected void setTime(CommandSender commandSender, Long setTime, Long displayTime, WeatherType setWeather) throws YiffBukkitCommandException {
		if (setTime == null) {
			playerHelper.frozenTimes.remove(commandSender.getName());
			playerHelper.sendDirectedMessage(commandSender, "Reset your time back to normal!");
		}
		else {
			playerHelper.frozenTimes.put(commandSender.getName(), setTime);
			playerHelper.sendDirectedMessage(commandSender, "You forced your time to be: " + displayTime + ":00");
		}

		if (setWeather == null) {
			playerHelper.frozenWeathers.remove(commandSender.getName());
			playerHelper.sendDirectedMessage(commandSender, "Reset your weather back to normal!");
		}
		else {
			playerHelper.frozenWeathers.put(commandSender.getName(), setWeather);
			playerHelper.sendDirectedMessage(commandSender, "You forced your weather to be: " + setWeather.name + ".");
		}

		playerHelper.pushWeather(asPlayer(commandSender));
	}
}
