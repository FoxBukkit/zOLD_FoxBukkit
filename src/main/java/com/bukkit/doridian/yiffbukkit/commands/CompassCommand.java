package com.bukkit.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import com.bukkit.doridian.yiffbukkit.YiffBukkit;

public class CompassCommand extends ICommand {
	String[] directions = { "N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW" };
	
	public int GetMinLevel() {
		return 0;
	}
	
	public CompassCommand(YiffBukkit plug) {
		plugin = plug;
	}

	public void Run(Player ply, String[] args, String argStr) {
		float yaw = (ply.getLocation().getYaw()%360+630)%360;
		
		int intdeg = Math.round(yaw / 22.5F);
        if (intdeg < 0) intdeg += 16;
        if (intdeg >= 16) intdeg -= 16;
		
		plugin.playerHelper.SendDirectedMessage(ply, "Direction: "+directions[intdeg]+" ("+Math.round(yaw)+")");
	}
	
	public String GetHelp() {
		return "Gives you your current bearing";
	}

	public String GetUsage() {
		return "";
	}
}
