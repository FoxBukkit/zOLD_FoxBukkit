package com.bukkit.doridian.yiffbukkit.commands;

import java.util.HashSet;
import java.util.Hashtable;

import org.bukkit.entity.Player;

import com.bukkit.doridian.yiffbukkit.YiffBukkit;

public class SummonCommand extends ICommand {
	HashSet<String> playerForbidsPort = new HashSet<String>();

	public int GetMinLevel() {
		return 2;
	}
	
	public SummonCommand(YiffBukkit plug) {
		plugin = plug;
	}

	public void Run(Player ply, String[] args, String argStr) {
		Player otherply = plugin.playerHelper.MatchPlayerSingle(ply, args[0]);
		if(otherply == null) return;
		
		int level = plugin.playerHelper.GetPlayerLevel(ply);
		int otherlevel = plugin.playerHelper.GetPlayerLevel(otherply);
		
		boolean denied = false;
		
		if(level < otherlevel) {
			denied = true;
		}
		else if (level == otherlevel) {
			if (playerForbidsPort.contains(otherply.getName())) {
				denied = true;
			}
		}
		
		if (denied) {
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
