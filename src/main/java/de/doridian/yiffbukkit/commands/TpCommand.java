package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.util.PlayerFindException;

public class TpCommand extends ICommand {
	public int GetMinLevel() {
		return 1;
	}

	public TpCommand(YiffBukkit plug) {
		super(plug);
	}

	public void Run(Player ply, String[] args, String argStr) throws PlayerFindException {
		Player otherply = playerHelper.MatchPlayerSingle(args[0]);

		String playerName = ply.getName();
		String otherName = otherply.getName();

		if (!playerHelper.CanTp(ply, otherply)) {
			playerHelper.SendPermissionDenied(ply);
			return;
		}

		ply.teleportTo(otherply);

		if (playerHelper.vanishedPlayers.contains(playerName)) {
			playerHelper.SendServerMessage(playerName + " teleported to " + otherName, 3);
		}
		else {
			playerHelper.SendServerMessage(playerName + " teleported to " + otherName);
		}
	}

	public String GetHelp() {
		return "Teleports you to the specified user";
	}

	public String GetUsage() {
		return "<name>";
	}
}
