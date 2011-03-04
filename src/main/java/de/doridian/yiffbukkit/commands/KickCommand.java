package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.util.Utils;

public class KickCommand extends ICommand {
	public int GetMinLevel() {
		return 2;
	}

	public KickCommand(YiffBukkit plug) {
		super(plug);
	}

	public void Run(Player ply, String[] args, String argStr) {
		Player otherply = playerHelper.MatchPlayerSingle(ply, args[0]);
		if(otherply == null) return;

		String reason = Utils.concatArray(args, 1, "Kicked by " + ply.getName());

		if(playerHelper.GetPlayerLevel(ply) < playerHelper.GetPlayerLevel(otherply)) {
			playerHelper.SendPermissionDenied(ply);
			return;
		}

		otherply.kickPlayer(reason);
		playerHelper.SendServerMessage(ply.getName() + " kicked " + otherply.getName() + " (reason: "+reason+")");
	}

	public String GetHelp() {
		return "Kicks specified user";
	}

	public String GetUsage() {
		return "<name> [reason here]";
	}
}
