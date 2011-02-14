package com.bukkit.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import com.bukkit.doridian.yiffbukkit.YiffBukkit;

public class SummonCommand extends ICommand {
	public int GetMinLevel() {
		return 2;
	}
	
	public SummonCommand(YiffBukkit plug) {
		plugin = plug;
	}

	public void Run(Player ply, String[] args, String argStr) {
		Player otherply = plugin.playerHelper.MatchPlayerSingle(ply, args[0]);
		if(otherply == null) return;
		if(plugin.playerHelper.GetPlayerLevel(ply) < plugin.playerHelper.GetPlayerLevel(otherply)) {
			plugin.playerHelper.SendPermissionDenied(ply);
			return;
		}
		
		otherply.teleportTo(ply);
		
		plugin.playerHelper.SendServerMessage(ply.getName() + " summoned " + otherply.getName());
	}
	
	public String GetHelp() {
		return "Teleports the specified user to you";
	}

	public String GetUsage() {
		return "<name>";
	}
}
