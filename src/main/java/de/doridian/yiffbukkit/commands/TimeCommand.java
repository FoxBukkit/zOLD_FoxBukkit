package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("time")
@Help("Forces/fixes current time *clientside*.")
@Usage("[normal|night|day|morning|afternoon|<0-23>]")
@Level(1)
public class TimeCommand extends ServerTimeCommand {
	@Override
	protected void setTime(Player ply, Long setTime, Long displayTime) {
		if (setTime == null) {
			playerHelper.frozenTimes.remove(ply.getName());
			playerHelper.SendDirectedMessage(ply, "Reset your time back to normal!");
		}
		else {
			playerHelper.frozenTimes.put(ply.getName(), setTime);
			playerHelper.SendDirectedMessage(ply, "You forced your time to be: " + displayTime + ":00");
		}
	}

	@Override
	public String GetHelp() {
		return "Forces/fixes current time *clientside*.";
	}
}
