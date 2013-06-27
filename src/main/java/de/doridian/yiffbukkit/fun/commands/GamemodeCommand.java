package de.doridian.yiffbukkit.fun.commands;

import de.doridian.yiffbukkit.main.PermissionDeniedException;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;
import de.doridian.yiffbukkit.main.util.MultiplePlayersFoundException;
import de.doridian.yiffbukkit.main.util.PlayerNotFoundException;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

@Names({"gamemode", "gm"})
@Help("Sets the gamemode (creative / survival) for a player (default: you)")
@Usage("<gamemode> [player]")
@Permission("yiffbukkit.gamemode.self")
public class GamemodeCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		final Player target;
		final GameMode gameMode;

		switch (args.length) {
		case 0:
			target = ply;
			gameMode = toggleGameMode(target);
			break;

		case 1: {
			final GameMode firstArgGameMode = getGameMode(args[0]);
			if (firstArgGameMode == null) {
				// number|modename
				target = getPlayer(args[0]);
				gameMode = toggleGameMode(target);
			}
			else {
				target = ply;
				gameMode = getGameMode(args[0]);
			}
			break;
		}

		default: {
			final GameMode firstArgGameMode = getGameMode(args[0]);
			if (firstArgGameMode == null) {
				// playername number|modename
				target = getPlayer(args[0]);
				gameMode = getGameMode(args[1]);
			}
			else {
				// number|modename playername
				target = getPlayer(args[1]);
				gameMode = firstArgGameMode;
			}
		}
		}

		if (gameMode == null)
			throw new YiffBukkitCommandException("Invalid gamemode specified");

		if (target != ply && !ply.hasPermission("yiffbukkit.gamemode.others"))
			throw new PermissionDeniedException();

		target.setGameMode(gameMode);

		if (target == ply) {
			plugin.playerHelper.sendServerMessage(ply.getName() + " changed their gamemode to " + gameMode.toString());
		}
		else {
			plugin.playerHelper.sendServerMessage(ply.getName() + " changed the gamemode of " + target.getName() + " to " + gameMode.toString());
		}
	}

	private GameMode toggleGameMode(Player player) {
		switch(player.getGameMode()) {
		case SURVIVAL:
			return GameMode.CREATIVE;

		default:
			return GameMode.SURVIVAL;
		}
	}

	private Player getPlayer(String arg) throws PlayerNotFoundException, MultiplePlayersFoundException {
		return plugin.playerHelper.matchPlayerSingle(arg);
	}

	private GameMode getGameMode(String arg) {
		final char firstChar = Character.toUpperCase(arg.charAt(0));
		int numeric = -1;
		try {
			numeric = Integer.parseInt(arg);
		}
		catch (NumberFormatException e) { }

		for (GameMode gameMode : GameMode.values()) {
			if (gameMode.name().charAt(0) == firstChar || gameMode.getValue() == numeric) {
				return gameMode;
			}
		}

		return null;
	}
}
