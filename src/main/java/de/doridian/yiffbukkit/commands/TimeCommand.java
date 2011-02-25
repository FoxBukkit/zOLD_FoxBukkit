package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

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
	protected void setTime(Player ply, Long setTime, Long displayTime) {
		if (setTime == null) {
			plugin.playerHelper.frozenTimes.remove(ply.getName());
			plugin.playerHelper.SendDirectedMessage(ply, "Reset Your Time back to normal!");
		}
		else {
			plugin.playerHelper.frozenTimes.put(ply.getName(), setTime);
			plugin.playerHelper.SendDirectedMessage(ply, "You Forced Your Time to be: " + displayTime + ":00");
		}
	}

	public String GetHelp() {
		return "Forces/fixes current time *clientside*.";
	}
}
