package de.doridian.yiffbukkit.commands;

import java.util.Hashtable;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkit;

public class ServerTimeCommand extends ICommand {
	Hashtable<String,Long> timeSwatches = new Hashtable<String,Long>();
	{
		timeSwatches.put("night", 0L);
		timeSwatches.put("morning", 6L);
		timeSwatches.put("day", 12L);
		timeSwatches.put("afternoon", 18L);
	};


	public int GetMinLevel() {
		return 3;
	}

	public ServerTimeCommand(YiffBukkit plug) {
		super(plug);
	}

	public void Run(Player ply, String[] args, String argStr) {
		long displayTime;

		if (argStr.isEmpty() || argStr.equalsIgnoreCase("normal")) {
			setTime(ply, null, null);
			return;
		}
		else if (timeSwatches.containsKey(argStr.toLowerCase())) {
			displayTime = timeSwatches.get(argStr.toLowerCase());
		}
		else {
			try
			{
				displayTime = Integer.valueOf(args[1]);
			}
			catch (Exception e) {
				playerHelper.SendDirectedMessage(ply, "Usage: " + GetUsage());
				return;
			}
		}

		setTime(ply, ((displayTime+18)%24)*1000, displayTime);
	}

	protected void setTime(Player ply, Long setTime, Long displayTime) {
		playerHelper.frozenServerTime = setTime;
		if (setTime == null) {
			playerHelper.SendServerMessage(ply.getName() + " reset the Server Time back to normal!");
		}
		else {
			playerHelper.SendServerMessage(ply.getName() + " forced the Server Time to be: " + displayTime + ":00");
		}
	}

	public String GetHelp() {
		return "Forces/fixes current time *serverside*.";
	}

	public String GetUsage() {
		return "[normal|night|day|morning|afternoon|<0-23>]";
	}
}
