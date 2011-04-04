package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkit;

public class TimeCommand extends ServerTimeCommand {
	public int GetMinLevel() {
		return 1;
	}

	public TimeCommand(YiffBukkit plug) {
		super(plug);
	}

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

	public String GetHelp() {
		return "Forces/fixes current time *clientside*.";
	}
}
