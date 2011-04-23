package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.PermissionDeniedException;
import de.doridian.yiffbukkit.util.PlayerFindException;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names({"summon", "tphere"})
@Help("Teleports the specified user to you")
@Usage("<name>")
@Level(2)
public class SummonCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws PlayerFindException, PermissionDeniedException {
		Player otherply = playerHelper.MatchPlayerSingle(args[0]);

		if (!playerHelper.CanSummon(ply, otherply))
			throw new PermissionDeniedException();

		otherply.teleport(ply);

		playerHelper.SendServerMessage(ply.getName() + " summoned " + otherply.getName());
	}
}
