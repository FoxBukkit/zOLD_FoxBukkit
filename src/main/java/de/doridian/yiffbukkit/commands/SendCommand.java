package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.util.PlayerFindException;

public class SendCommand extends ICommand {
	public int GetMinLevel() {
		return 3;
	}

	public SendCommand(YiffBukkit plug) {
		super(plug);
	}

	public void Run(Player ply, String[] args, String argStr) throws PlayerFindException {
		Player fromPlayer = playerHelper.MatchPlayerSingle(args[0]);
		
		Player toPlayer = playerHelper.MatchPlayerSingle(args[1]);

		if (!playerHelper.CanSummon(ply, fromPlayer)) {
			playerHelper.SendPermissionDenied(ply);
			return;
		}

		if (!playerHelper.CanTp(ply, toPlayer)) {
			playerHelper.SendPermissionDenied(ply);
			return;
		}

		fromPlayer.teleportTo(toPlayer);

		playerHelper.SendServerMessage(ply.getName() + " sent " + fromPlayer.getName() + " sent " + toPlayer.getName());
	}

	public String GetHelp() {
		return "Teleports the specified user to you";
	}

	public String GetUsage() {
		return "<name>";
	}
}
