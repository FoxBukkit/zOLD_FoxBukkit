package de.doridian.yiffbukkit.commands;

import de.doridian.yiffbukkit.YiffBukkit;
import net.minecraft.server.MinecraftServer;

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
			//MinecraftServer.frozenTimes.remove(playerName);
		}
		else {
			//MinecraftServer.frozenTimes.put(playerName, settime);
		}
	}

	public String GetHelp() {
		return "Forces/fixes current time *clientside*.";
	}
}
