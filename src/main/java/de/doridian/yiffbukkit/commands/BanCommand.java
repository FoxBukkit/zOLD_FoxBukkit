package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.PermissionDeniedException;
import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.util.PlayerFindException;
import de.doridian.yiffbukkit.util.Utils;

public class BanCommand extends ICommand {
	public int GetMinLevel() {
		return 3;
	}

	public BanCommand(YiffBukkit plug) {
		super(plug);
	}

	public void Run(Player ply, String[] args, String argStr) throws PlayerFindException, PermissionDeniedException {
		Player otherply = playerHelper.MatchPlayerSingle(args[0]);

		String reason = Utils.concatArray(args, 1, "Kickbanned by " + ply.getName());

		if(playerHelper.GetPlayerLevel(ply) <= playerHelper.GetPlayerLevel(otherply))
			throw new PermissionDeniedException();

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
