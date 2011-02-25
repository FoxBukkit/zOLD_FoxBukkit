package de.doridian.yiffbukkit.commands;

import java.util.HashSet;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkit;

public class SummonCommand extends ICommand {
	HashSet<String> playerPortPermissions;

	public int GetMinLevel() {
		return 2;
	}

	public SummonCommand(YiffBukkit plug) {
		super(plug);
		playerPortPermissions = playerHelper.playerSummonPermissions;
	}

	public void Run(Player ply, String[] args, String argStr) {
		Player otherply = playerHelper.MatchPlayerSingle(ply, args[0]);
		if(otherply == null) return;

		String playerName = ply.getName();
		String otherName = otherply.getName();

		int level = playerHelper.GetPlayerLevel(ply);
		int otherlevel = playerHelper.GetPlayerLevel(otherply);

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
			playerHelper.SendPermissionDenied(ply);
			return;
		}

		otherply.teleportTo(ply);

		playerHelper.SendServerMessage(ply.getName() + " summoned " + otherply.getName());
	}

	public String GetHelp() {
		return "Teleports the specified user to you";
	}

	public String GetUsage() {
		return "<name>";
	}
}
