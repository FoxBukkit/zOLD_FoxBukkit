package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.PermissionDeniedException;
import de.doridian.yiffbukkit.YiffBukkitPlayerListener;
import de.doridian.yiffbukkit.util.PlayerFindException;

public class SendCommand extends ICommand {
	public int GetMinLevel() {
		return 3;
	}

	public SendCommand(YiffBukkitPlayerListener playerListener) {
		super(playerListener);
	}

	public void Run(Player ply, String[] args, String argStr) throws PlayerFindException, PermissionDeniedException {
		Player fromPlayer = playerHelper.MatchPlayerSingle(args[0]);

		Player toPlayer = playerHelper.MatchPlayerSingle(args[1]);

		if (!playerHelper.CanSummon(ply, fromPlayer))
			throw new PermissionDeniedException();

		if (!playerHelper.CanTp(ply, toPlayer))
			throw new PermissionDeniedException();

		fromPlayer.teleport(toPlayer);

		playerHelper.SendServerMessage(ply.getName() + " sent " + fromPlayer.getName() + " to " + toPlayer.getName());
	}

	public String GetHelp() {
		return "Teleports the specified user to you";
	}

	public String GetUsage() {
		return "<name>";
	}
}
