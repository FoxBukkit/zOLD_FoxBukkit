package de.doridian.yiffbukkit.fun.commands;

import de.doridian.yiffbukkit.core.util.PlayerHelper;
import de.doridian.yiffbukkit.main.PermissionDeniedException;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;
import org.bukkit.entity.Player;

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
			PlayerHelper.sendServerMessage(ply.getName() + " leashed " + otherply.getName());
		else
			PlayerHelper.sendServerMessage(ply.getName() + " unleashed " + otherply.getName());
	}
}
