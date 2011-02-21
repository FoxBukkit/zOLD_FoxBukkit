package com.bukkit.doridian.yiffbukkit.commands;

import java.util.Hashtable;

import org.bukkit.entity.Player;

import com.bukkit.doridian.yiffbukkit.YiffBukkit;

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
		String playerName = ply.getName();

		long settime;

		if (argStr.isEmpty() || argStr.equalsIgnoreCase("normal")) {
			setTime(playerName, null);
			return;
		}
		else if (timeSwatches.containsKey(argStr.toLowerCase())) {
			settime = timeSwatches.get(argStr.toLowerCase());
		}
		else {
			try
			{
				settime = Integer.valueOf(args[1]);
			}
			catch (Exception e) {
				plugin.playerHelper.SendDirectedMessage(ply, "Usage: " + GetUsage());
				return;
			}
		}

		settime = ((settime+18)%24)*1000;

		setTime(playerName, settime);
	}

	protected void setTime(String playerName, Long settime) {
		plugin.playerHelper.frozenServerTime = settime;
	}

	public String GetHelp() {
		return "Forces/fixes current time *serverside*.";
	}

	public String GetUsage() {
		return "[normal|night|day|morning|afternoon|<0-23>]";
	}
}
