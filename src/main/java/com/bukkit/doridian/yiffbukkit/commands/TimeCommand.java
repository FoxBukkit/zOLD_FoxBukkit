package com.bukkit.doridian.yiffbukkit.commands;

import java.util.Hashtable;

import org.bukkit.entity.Player;

import com.bukkit.doridian.yiffbukkit.YiffBukkit;

public class TimeCommand extends ICommand {
	Hashtable<String,Long> timeSwatches = new Hashtable<String,Long>();
	{
		timeSwatches.put("night", 0L);
		timeSwatches.put("morning", 6L);
		timeSwatches.put("day", 12L);
		timeSwatches.put("afternoon", 18L);
	};
	

	public int GetMinLevel() {
		return 0;
	}
	
	public TimeCommand(YiffBukkit plug) {
		plugin = plug;
	}

	public void Run(Player ply, String[] args, String argStr) {
		String playerName = ply.getName();
		
		long settime;
		
		if (argStr.isEmpty() || argStr.equalsIgnoreCase("normal")) {
			plugin.playerHelper.frozenTimes.remove(playerName);
			return;
		}
		else if (timeSwatches.containsKey(argStr.toLowerCase())) {
			settime = timeSwatches.get(argStr.toLowerCase());
		}
		else {
			try
			{
				settime = Integer.valueOf(args[1]);
				plugin.playerHelper.frozenTimes.remove(playerName);
			}
			catch (Exception e) {
				plugin.playerHelper.SendDirectedMessage(ply, "Usage: /time " + GetUsage());
				return;
			}
        }
		
        settime = ((settime+18)%24)*1000;
		
		plugin.playerHelper.frozenTimes.put(playerName, settime);
	}

	public String GetHelp() {
		return "Prints user list if used without parameters or information about the specified user";
	}

	public String GetUsage() {
		return "[normal|night|day|morning|afternoon|<0-23>]";
	}
}
