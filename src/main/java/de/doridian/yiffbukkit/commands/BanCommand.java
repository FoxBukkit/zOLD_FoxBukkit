package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.Utils;
import de.doridian.yiffbukkit.YiffBukkit;

public class BanCommand extends ICommand {
	public int GetMinLevel() {
		return 3;
	}

	public BanCommand(YiffBukkit plug) {
		super(plug);
	}

	public void Run(Player ply, String[] args, String argStr) {
		Player otherply = playerHelper.MatchPlayerSingle(ply, args[0]);
		if(otherply == null) return;

		String reason = Utils.concatArray(args, 1, "Kickbanned by " + ply.getName());

		if(playerHelper.GetPlayerLevel(ply) <= playerHelper.GetPlayerLevel(otherply)) {
			playerHelper.SendPermissionDenied(ply);
			return;
		}

		playerHelper.SetPlayerRank(otherply.getName(), "banned");
		otherply.kickPlayer(reason);
		playerHelper.SendServerMessage(ply.getName() + " kickbanned " + otherply.getName() + " (reason: "+reason+")");
	}

	public String GetHelp() {
		return "Bans specified user";
	}

	public String GetUsage() {
		return "<name> [reason here]";
	}
}
