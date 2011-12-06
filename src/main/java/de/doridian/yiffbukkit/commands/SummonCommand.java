package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.PermissionDeniedException;
import de.doridian.yiffbukkit.util.PlayerFindException;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names({"summon", "tphere"})
@Help("Teleports the specified user to you")
@Usage("<name>")
@Permission("yiffbukkit.teleport.summon")
public class SummonCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws PlayerFindException, PermissionDeniedException {
		Player otherply = playerHelper.matchPlayerSingle(args[0]);

		if (!playerHelper.canSummon(ply, otherply))
			throw new PermissionDeniedException();

		plugin.playerHelper.teleportWithHistory(otherply, ply);

		playerHelper.sendServerMessage(ply.getName() + " summoned " + otherply.getName());
	}
}
