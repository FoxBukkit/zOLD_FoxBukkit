package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.PermissionDeniedException;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.YiffBukkitPlayerListener;

public class LeashCommand extends ICommand {

	public LeashCommand(YiffBukkitPlayerListener playerListener) {
		super(playerListener);
	}

	@Override
	public int GetMinLevel() {
		return 4;
	}

	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		if (args.length < 1)
			throw new YiffBukkitCommandException("Not enough arguments");
		
		Player otherply = playerHelper.MatchPlayerSingle(args[0]);

		if (!playerHelper.CanSummon(ply, otherply))
			throw new PermissionDeniedException();

		if (playerHelper.toggleLeash(ply, otherply))
			playerHelper.SendServerMessage(ply.getName() + " leashed " + otherply.getName());
		else
			playerHelper.SendServerMessage(ply.getName() + " unleashed " + otherply.getName());
	}

	@Override
	public String GetHelp() {
		return "Leashes or unleashes a player.";
	}

	@Override
	public String GetUsage() {
		// TODO Auto-generated method stub
		return "<name>";
	}
}
