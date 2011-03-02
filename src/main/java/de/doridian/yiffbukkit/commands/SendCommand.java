package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkit;

public class SendCommand extends ICommand {
	public int GetMinLevel() {
		return 3;
	}

	public SendCommand(YiffBukkit plug) {
		super(plug);
	}

	public void Run(Player ply, String[] args, String argStr) {
		Player fromPlayer = playerHelper.MatchPlayerSingle(ply, args[0]);
		if (fromPlayer == null) return;
		
		Player toPlayer = playerHelper.MatchPlayerSingle(ply, args[1]);
		if (toPlayer == null) return;

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
