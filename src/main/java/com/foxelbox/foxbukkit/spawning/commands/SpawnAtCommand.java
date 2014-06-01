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
package com.foxelbox.foxbukkit.spawning.commands;

import com.foxelbox.foxbukkit.main.FoxBukkitCommandException;
import com.foxelbox.foxbukkit.main.commands.system.ICommand;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Help;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Names;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Permission;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Usage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@Names("spawnat")
@Help(
		"Spawns a mob at the specified location.\n" +
		"Missing values will be replaced by a default, if that's available.\n" +
		" Offsets from that default can be specified by prefixing an 'o'."
)
@Usage("[<x>][,<y>][,<z>][,<yaw>][,<pitch>][,<world>][,<velX>][,<velY>][,<velZ>] <mob>")
@ICommand.NumericFlags("m")
@Permission("foxbukkit.spawnat")
public class SpawnAtCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr, String commandName) throws FoxBukkitCommandException {
		args = parseFlags(args);

		final int amount;
		if (numericFlags.containsKey('m')) {
			final int maxItems = commandSender.hasPermission("foxbukkit.throw.unlimited") ? 1000 : 10;
			amount = Math.max(1, Math.min(maxItems, (int) (double) numericFlags.get('m')));
		}
		else {
			amount = 1;
		}

		final String coordString;
		final String mobString;
		final String themString;
		switch (args.length) {
		case 0:
			throw new FoxBukkitCommandException("Not enough arguments!");

		case 1:
			coordString = "";
			mobString = args[0];
			themString = null; // TODO: detect if coordString or name given ...somehow
			break;

		case 2:
			coordString = args[0];
			mobString = args[1];
			themString = null;
			break;

		default:
			coordString = args[0];
			mobString = args[1];
			themString = args[2];
			break;
		}

		final Player player;
		if (themString != null) {
			player = playerHelper.matchPlayerSingle(themString);
		}
		else if (commandSender instanceof Player) {
			player = (Player) commandSender;
		}
		else {
			player = null;
		}

		final Location defaultLocation = getCommandSenderLocation(commandSender, true, new Location(null, 0, 0, 0));

		final String[] coords = coordString.split(",");

		final Location location = new Location(
				getWorld(coords, 5, defaultLocation.getWorld()),
				getDouble(coords, 0, defaultLocation.getX()),
				getDouble(coords, 1, defaultLocation.getY()),
				getDouble(coords, 2, defaultLocation.getZ()),
				(float) getDouble(coords, 3, defaultLocation.getPitch()),
				(float) getDouble(coords, 4, defaultLocation.getYaw())
		);
		final Vector velocity = new Vector(
			getDouble(coords, 6, 0),
			getDouble(coords, 7, 0),
			getDouble(coords, 8, 0)
		);

		for(int i=0;i<amount;i++) {
			final Entity entity = plugin.spawnUtils.buildMob(mobString.split("\\+"), commandSender, player, location);
			entity.setVelocity(velocity);
		}
	}

	private static String getString(String[] coords, int index, String defaultValue) {
		if (index >= coords.length)
			return defaultValue;

		return coords[index];
	}

	private static double getDouble(String[] coords, int index, double defaultValue) throws FoxBukkitCommandException {
		final String string = getString(coords, index, "");
		if (string.isEmpty())
			return defaultValue;

		try {
			if (string.charAt(0) == 'o') {
				return defaultValue + Double.parseDouble(string.substring(1));
			}
			else {
				return Double.parseDouble(string);
			}
		} catch (NumberFormatException e) {
			throw new FoxBukkitCommandException("Invalid number format!", e);
		}
	}

	private static World getWorld(String[] coords, int index, World defaultValue) throws FoxBukkitCommandException {
		final World world = Bukkit.getWorld(getString(coords, index, defaultValue.getName()));
		if (world == null)
			throw new FoxBukkitCommandException("World not found!");

		return world;
	}
}
