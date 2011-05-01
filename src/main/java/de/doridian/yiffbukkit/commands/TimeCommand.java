package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.commands.ICommand.*;
import de.doridian.yiffbukkit.util.PlayerHelper.WeatherType;

@Names("time")
@Help("Forces/fixes current time *clientside*.")
@Usage("[normal|night|day|morning|afternoon|<0-23>]")
@Level(1)
@StringFlags("w")
public class TimeCommand extends ServerTimeCommand {
	@Override
	protected void setTime(Player ply, Long setTime, Long displayTime, WeatherType setWeather) {
		if (setTime == null) {
			playerHelper.frozenTimes.remove(ply.getName());
			playerHelper.SendDirectedMessage(ply, "Reset your time back to normal!");
		}
		else {
			playerHelper.frozenTimes.put(ply.getName(), setTime);
			playerHelper.SendDirectedMessage(ply, "You forced your time to be: " + displayTime + ":00");
		}

		if (setWeather == null) {
			playerHelper.frozenWeathers.remove(ply.getName());
			playerHelper.SendDirectedMessage(ply, "Reset your weather back to normal!");
		}
		else {
			playerHelper.frozenWeathers.put(ply.getName(), setWeather);
			playerHelper.SendDirectedMessage(ply, "You forced your weather to be: " + setWeather.name + ".");
		}
		playerHelper.pushWeather(ply);
	}

	@Override
	public String GetHelp() {
		return "Forces/fixes current time *clientside*.";
	}
}
