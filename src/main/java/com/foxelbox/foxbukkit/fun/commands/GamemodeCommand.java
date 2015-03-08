/**
 * This file is part of FoxBukkit.
 *
 * FoxBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoxBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FoxBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.foxelbox.foxbukkit.fun.commands;

import com.foxelbox.foxbukkit.core.util.PlayerHelper;
import com.foxelbox.foxbukkit.main.FoxBukkitCommandException;
import com.foxelbox.foxbukkit.main.PermissionDeniedException;
import com.foxelbox.foxbukkit.main.commands.system.ICommand;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Help;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Names;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Permission;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Usage;
import com.foxelbox.foxbukkit.main.util.MultiplePlayersFoundException;
import com.foxelbox.foxbukkit.main.util.PlayerNotFoundException;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

@Names({"gamemode", "gm"})
@Help("Sets the gamemode (creative / survival) for a player (default: you)")
@Usage("<gamemode> [player]")
@Permission("foxbukkit.gamemode.self")
public class GamemodeCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr, String commandName) throws FoxBukkitCommandException {
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
			throw new FoxBukkitCommandException("Invalid gamemode specified");

		if (target != ply && !ply.hasPermission("foxbukkit.gamemode.others"))
			throw new PermissionDeniedException();

		target.setGameMode(gameMode);

		if (target == ply && !ply.hasPermission("foxbukkit.gamemode.silent")) {
			PlayerHelper.sendServerMessage(ply.getName() + " changed their gamemode to " + gameMode.toString());
		}
		else {
			PlayerHelper.sendServerMessage(ply.getName() + " changed the gamemode of " + target.getName() + " to " + gameMode.toString());
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
		arg = arg.toUpperCase();
		int numeric = -1;
		try {
			numeric = Integer.parseInt(arg);
		}
		catch (NumberFormatException ignored) { }

		for (GameMode gameMode : GameMode.values()) {
			if (gameMode.name().startsWith(arg) || gameMode.getValue() == numeric) {
				return gameMode;
			}
		}

		return null;
	}
}
