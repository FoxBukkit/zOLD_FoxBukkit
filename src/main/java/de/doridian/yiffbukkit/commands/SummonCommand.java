package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.PermissionDeniedException;
import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.util.PlayerFindException;

public class SummonCommand extends ICommand {
	public int GetMinLevel() {
		return 2;
	}

	public SummonCommand(YiffBukkit plug) {
		super(plug);
	}

	public void Run(Player ply, String[] args, String argStr) throws PlayerFindException, PermissionDeniedException {
		Player otherply = playerHelper.MatchPlayerSingle(args[0]);

		if (!playerHelper.CanSummon(ply, otherply))
			throw new PermissionDeniedException();

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
