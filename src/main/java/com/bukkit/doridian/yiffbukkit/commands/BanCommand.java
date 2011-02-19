package com.bukkit.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import com.bukkit.doridian.yiffbukkit.Utils;
import com.bukkit.doridian.yiffbukkit.YiffBukkit;

public class BanCommand extends ICommand {
	public int GetMinLevel() {
		return 3;
	}
	
	public BanCommand(YiffBukkit plug) {
		super(plug);
	}

	public void Run(Player ply, String[] args, String argStr) {
		Player otherply = plugin.playerHelper.MatchPlayerSingle(ply, args[0]);
		if(otherply == null) return;
		
		String reason = Utils.concatArray(args, 1, "Kickbanned by " + ply.getName());
		
		if(plugin.playerHelper.GetPlayerLevel(ply) < plugin.playerHelper.GetPlayerLevel(otherply)) {
			plugin.playerHelper.SendPermissionDenied(ply);
			return;
		}
		
		plugin.playerHelper.SetPlayerRank(otherply.getName(), "banned");
		otherply.kickPlayer(reason);
		plugin.playerHelper.SendServerMessage(ply.getName() + " kickbanned " + otherply.getName() + " (reason: "+reason+")");
	}
	
	public String GetHelp() {
		return "Bans specified user";
	}

	public String GetUsage() {
		return "<name> [reason here]";
	}
}
