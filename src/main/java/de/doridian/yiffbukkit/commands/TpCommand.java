package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.PermissionDeniedException;
import de.doridian.yiffbukkit.YiffBukkitPlayerListener;
import de.doridian.yiffbukkit.util.PlayerFindException;

public class TpCommand extends ICommand {
	public int GetMinLevel() {
		return 1;
	}

	public TpCommand(YiffBukkitPlayerListener playerListener) {
		super(playerListener);
	}

	public void Run(Player ply, String[] args, String argStr) throws PlayerFindException, PermissionDeniedException {
		Player otherply = playerHelper.MatchPlayerSingle(args[0]);

		String playerName = ply.getName();
		String otherName = otherply.getName();

		if (!playerHelper.CanTp(ply, otherply))
			throw new PermissionDeniedException();

		ply.teleport(otherply);

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
