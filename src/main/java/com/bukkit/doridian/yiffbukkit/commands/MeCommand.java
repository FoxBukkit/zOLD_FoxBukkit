package com.bukkit.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import com.bukkit.doridian.yiffbukkit.YiffBukkit;

public class MeCommand extends ICommand {
	public int GetMinLevel() {
		return 0;
	}
	
	public MeCommand(YiffBukkit plug) {
		super(plug);
	}

	public void Run(Player ply, String[] args, String argStr) {
		plugin.getServer().broadcastMessage(plugin.playerHelper.GetPlayerTag(ply) + ply.getName() + " " + argStr);
	}
	
	public String GetHelp() {
		return "Well, its /me, durp";
	}

	public String GetUsage() {
		return "<stuff here>";
	}
}
