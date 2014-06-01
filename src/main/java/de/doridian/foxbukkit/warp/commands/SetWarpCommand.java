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
package de.doridian.foxbukkit.warp.commands;

import de.doridian.foxbukkit.core.util.PlayerHelper;
import de.doridian.foxbukkit.main.FoxBukkitCommandException;
import de.doridian.foxbukkit.main.commands.system.ICommand;
import de.doridian.foxbukkit.main.commands.system.ICommand.Cost;
import de.doridian.foxbukkit.main.commands.system.ICommand.Help;
import de.doridian.foxbukkit.main.commands.system.ICommand.Names;
import de.doridian.foxbukkit.main.commands.system.ICommand.Permission;
import de.doridian.foxbukkit.main.commands.system.ICommand.Usage;
import de.doridian.foxbukkit.warp.WarpDescriptor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.UUID;

@Names("setwarp")
@Help("Creates a warp point with the specified name, for the specified player or yourself. When run from the console, the warp is created at the guest spawn.")
@Usage("<warp point name> [<exact owner name>]")
@Permission("foxbukkit.warp.setwarp")
@Cost(300)
public class SetWarpCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr, String commandName) throws FoxBukkitCommandException {
		final String warpName;
		final UUID ownerName;

		switch (args.length) {
		case 0:
			throw new FoxBukkitCommandException("Not enough arguments");

		case 1:
			warpName = args[0];
			ownerName = asPlayer(commandSender).getUniqueId();
			break;

		default:
			warpName = args[0];
			ownerName = playerHelper.matchPlayerSingle(args[1]).getUniqueId();
		}

		WarpDescriptor warp = plugin.warpEngine.setWarp(ownerName, warpName, getWarpTargetLocation(commandSender));
		PlayerHelper.sendDirectedMessage(commandSender, "Created warp \u00a79" + warp.name + "\u00a7f. Use '/warp help' to see how to modify it.");
	}

	private Location getWarpTargetLocation(CommandSender commandSender) {
		final Location guestSpawn = playerHelper.getRankSpawnPosition(plugin.getOrCreateWorld("world", World.Environment.NORMAL), "guest");
		return getCommandSenderLocation(commandSender, false, guestSpawn);
	}
}
