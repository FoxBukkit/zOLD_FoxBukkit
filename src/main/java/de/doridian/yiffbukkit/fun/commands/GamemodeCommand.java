package de.doridian.yiffbukkit.fun.commands;

import de.doridian.yiffbukkit.main.PermissionDeniedException;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.ICommand;
import de.doridian.yiffbukkit.main.commands.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.ICommand.Usage;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

@Names({"gamemode", "gm"})
@Help("Sets the gamemode (creative / survival) for a player (default: you)")
@Usage("<gamemode> [player]")
@Permission("yiffbukkit.gamemode.self")
public class GamemodeCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		Player target = ply;
		if (args.length > 1)
			target = plugin.playerHelper.matchPlayerSingle(args[1]);

		if (target != ply && !ply.hasPermission("yiffbukkit.gamemode.others"))
			throw new PermissionDeniedException();

		final String arg = args[0].toUpperCase();
		final char firstChar = arg.charAt(0);
		int numeric = -1;
		try {
			numeric = Integer.parseInt(arg);
		}
		catch (NumberFormatException e) { }

		for (GameMode gameMode : GameMode.values()) {
			if (gameMode.name().charAt(0) == firstChar || gameMode.getValue() == numeric) {
				target.setGameMode(gameMode);

				if (target == ply) {
					plugin.playerHelper.sendServerMessage(ply.getName() + " changed their gamemode to " + gameMode.toString());
				}
				else {
					plugin.playerHelper.sendServerMessage(ply.getName() + " changed the gamemode of " + target.getName() + " to " + gameMode.toString());
				}

				return;
			}
		}

		throw new YiffBukkitCommandException("Invalid gamemode specified");
	}
}
