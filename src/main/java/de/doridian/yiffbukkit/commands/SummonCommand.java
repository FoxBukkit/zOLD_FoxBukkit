package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkit;

public class SummonCommand extends ICommand {
	public int GetMinLevel() {
		return 2;
	}

	public SummonCommand(YiffBukkit plug) {
		super(plug);
	}

	public void Run(Player ply, String[] args, String argStr) {
		Player otherply = playerHelper.MatchPlayerSingle(ply, args[0]);
		if (otherply == null) return;

		if (!playerHelper.CanSummon(ply, otherply)) {
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
