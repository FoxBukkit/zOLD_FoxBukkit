package com.bukkit.doridian.yiffbukkit.commands;

import java.util.HashSet;

import org.bukkit.entity.Player;

import com.bukkit.doridian.yiffbukkit.YiffBukkit;

public class TpCommand extends ICommand {
	HashSet<String> playerPortPermissions;
	
	public int GetMinLevel() {
		return 1;
	}
	
	public TpCommand(YiffBukkit plug) {
		super(plug);
		playerPortPermissions = plugin.playerHelper.playerTpPermissions;
	}

	public void Run(Player ply, String[] args, String argStr) {
		Player otherply = plugin.playerHelper.MatchPlayerSingle(ply, args[0]);
		if(otherply == null) return;
		
		String playerName = ply.getName();
		String otherName = otherply.getName();
		
		int level = plugin.playerHelper.GetPlayerLevel(ply);
		int otherlevel = plugin.playerHelper.GetPlayerLevel(otherply);
		
		boolean denied = false;
		
		if (level < otherlevel) {
			denied = true;
		}
		else if (level == otherlevel) {
			if (playerPortPermissions.contains(otherName)) {
				denied = true;
			}
		}
		
		if (playerPortPermissions.contains(otherName+" "+playerName))
			denied = false;
		
		if (denied) {
			plugin.playerHelper.SendPermissionDenied(ply);
			return;
		}
		
		ply.teleportTo(otherply);
		
		plugin.playerHelper.SendServerMessage(ply.getName() + " teleported to " + otherply.getName());
	}
	
	public String GetHelp() {
		return "Teleports you to the specified user";
	}

	public String GetUsage() {
		return "<name>";
	}
}
