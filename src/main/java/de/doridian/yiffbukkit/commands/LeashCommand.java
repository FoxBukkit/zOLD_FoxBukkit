package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.PermissionDeniedException;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("leash")
@Help("Leashes or unleashes a player.")
@Usage("<name>")
@Permission("yiffbukkit.players.leash")
public class LeashCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		if (args.length < 1)
			throw new YiffBukkitCommandException("Not enough arguments");

		Player otherply = playerHelper.matchPlayerSingle(args[0]);

		if (!playerHelper.canSummon(ply, otherply))
			throw new PermissionDeniedException();

		if (playerHelper.toggleLeash(ply, otherply))
			playerHelper.sendServerMessage(ply.getName() + " leashed " + otherply.getName());
		else
			playerHelper.sendServerMessage(ply.getName() + " unleashed " + otherply.getName());
	}
}
