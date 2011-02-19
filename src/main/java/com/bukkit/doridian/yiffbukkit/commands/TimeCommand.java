package com.bukkit.doridian.yiffbukkit.commands;

import com.bukkit.doridian.yiffbukkit.YiffBukkit;

public class TimeCommand extends ServerTimeCommand {
	public int GetMinLevel() {
		return 1;
	}
	
	public TimeCommand(YiffBukkit plug) {
		super(plug);
	}

	@Override
	protected void setTime(String playerName, Long settime) {
		if (settime == null) {
			plugin.playerHelper.frozenTimes.remove(playerName);
		}
		else {
			plugin.playerHelper.frozenTimes.put(playerName, settime);
		}
	}

	public String GetHelp() {
		return "Forces/fixes current time *clientside*.";
	}
}
