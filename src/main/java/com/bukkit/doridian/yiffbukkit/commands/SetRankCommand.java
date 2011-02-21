package com.bukkit.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import com.bukkit.doridian.yiffbukkit.YiffBukkit;

public class SetRankCommand extends ICommand {
	public int GetMinLevel() {
		return 3;
	}

	public SetRankCommand(YiffBukkit plug) {
		super(plug);
	}

	public void Run(Player ply, String[] args, String argStr) {
		String otherply = args[0];
		String newrank = args[1];
		int selflvl = plugin.playerHelper.GetPlayerLevel(ply);
		if(selflvl <= plugin.playerHelper.GetPlayerLevel(otherply) || selflvl <= plugin.playerHelper.GetRankLevel(newrank)) {
			plugin.playerHelper.SendPermissionDenied(ply);
			return;
		}
		plugin.playerHelper.SetPlayerRank(otherply, newrank);
		plugin.playerHelper.SendServerMessage(ply.getName() + " set rank of " + otherply + " to " + newrank);
	}

	public String GetHelp() {
		return "Sets rank of specified user";
	}

	public String GetUsage() {
		return "<full name> <rank>";
	}
}
