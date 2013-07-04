package de.doridian.yiffbukkit.spawning.commands;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Names("spawnat")
@Help(
		"Spawns a mob at the specified location.\n" +
		"Missing values will be replaced by a default, if that's available.\n" +
		" Offsets from that default can be specified by prefixing an 'o'."
)
@Usage("[<x>][,<y>][,<z>][,<yaw>][,<pitch>][,<world>] <mob>")
@Permission("yiffbukkit.spawnat")
public class SpawnAtCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		final String coordString;
		final String mobString;
		switch (args.length) {
		case 0:
			throw new YiffBukkitCommandException("Not enough arguments!");

		case 1:
			coordString = "";
			mobString = args[0];
			break;

		default:
			coordString = args[0];
			mobString = args[1];
			break;
		}

		final Player player;
		if (commandSender instanceof Player) {
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

		plugin.spawnUtils.buildMob(mobString.split("\\+"), commandSender, player, location);
	}

	private static String getString(String[] coords, int index, String defaultValue) {
		if (index >= coords.length)
			return defaultValue;

		return coords[index];
	}

	private static double getDouble(String[] coords, int index, double defaultValue) throws YiffBukkitCommandException {
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
			throw new YiffBukkitCommandException("Invalid number format!", e);
		}
	}

	private static World getWorld(String[] coords, int index, World defaultValue) throws YiffBukkitCommandException {
		final World world = Bukkit.getWorld(getString(coords, index, defaultValue.getName()));
		if (world == null)
			throw new YiffBukkitCommandException("World not found!");

		return world;
	}
}
