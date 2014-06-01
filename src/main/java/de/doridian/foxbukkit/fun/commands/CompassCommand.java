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
package de.doridian.foxbukkit.fun.commands;

import de.doridian.foxbukkit.core.util.AutoCleanup;
import de.doridian.foxbukkit.core.util.PlayerHelper;
import de.doridian.foxbukkit.main.FoxBukkitCommandException;
import de.doridian.foxbukkit.main.PermissionDeniedException;
import de.doridian.foxbukkit.main.commands.system.ICommand;
import de.doridian.foxbukkit.main.commands.system.ICommand.Help;
import de.doridian.foxbukkit.main.commands.system.ICommand.Names;
import de.doridian.foxbukkit.main.commands.system.ICommand.Permission;
import de.doridian.foxbukkit.main.commands.system.ICommand.Usage;
import de.doridian.foxbukkit.main.util.Utils;
import de.doridian.foxbukkit.warp.WarpDescriptor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@Names("compass")
@Help(
		"Gives the current bearing or sets the compass target.\n" +
		"Direction can be a direction acronym, like N or NE,\n" +
		"or a full direction name, like north_east or northeast."
)
@Usage("[spawn|home|here|player <name>|warp <name>|<direction>]")
@Permission("foxbukkit.compass")
public class CompassCommand extends ICommand {
	private int taskId = -1;
	private final Map<Player, Player> playerCompassTargets = new HashMap<>();
	{
		AutoCleanup.registerPlayerMap(playerCompassTargets);
	}

	@Override
	public void Run(Player ply, String[] args, String argStr, String commandName) throws FoxBukkitCommandException {
		if (args.length == 0) {
			final float yaw = ply.getLocation().getYaw();
			PlayerHelper.sendDirectedMessage(ply, "Direction: "+ Utils.yawToDirection(yaw)+" ("+Math.round((yaw+720)%360)+")");
			return;
		}

		switch (args[0]) {
		case "player":
		case "pl":
			if (!ply.hasPermission("foxbukkit.compass.player"))
				throw new PermissionDeniedException();

			if (args.length < 2)
				throw new FoxBukkitCommandException("Expected player name");

			final Player target = playerHelper.matchPlayerSingle(args[1]);

			if (!playerHelper.canTp(ply, target))
				throw new PermissionDeniedException();

			addCompassTarget(ply, target);

			PlayerHelper.sendDirectedMessage(ply, "Set your compass to follow "+target.getName()+".");
			return;
		}

		removeCompassTarget(ply);

		final Location location;
		switch (args[0]) {
		case "spawn":
			location = ply.getWorld().getSpawnLocation();
			break;

		case "home":
			final String posName;
			if (args.length < 2)
				posName = "default";
			else
				posName = args[1];

			location = playerHelper.getPlayerHomePosition(ply, posName);
			break;

		case "here":
			location = ply.getLocation();
			break;

		case "warp":
			if (args.length < 2)
				throw new FoxBukkitCommandException("Expected warp name");

			final WarpDescriptor warpDescriptor = plugin.warpEngine.getWarp(ply, args[1]);

			location = warpDescriptor.location;
			break;

		default:
			final int xmod;
			final int zmod;
			switch (args[0].length()) {
			case 2:
				switch (args[0].charAt(0)) {
				case 'n':
				case 'N':
					xmod = -1000000000;
					break;

				case 's':
				case 'S':
					xmod = 1000000000;
					break;

				default:
					throw new FoxBukkitCommandException("Unrecognised parameter");
				}

				switch (args[0].charAt(1)) {
				case 'e':
				case 'E':
					zmod = -1000000000;
					break;

				case 'w':
				case 'W':
					zmod = 1000000000;
					break;

				default:
					throw new FoxBukkitCommandException("Unrecognised parameter");
				}

				location = new Location(ply.getWorld(), xmod, 0, zmod);
				break;

			case 1:
				switch (args[0].charAt(0)) {
				case 'n':
				case 'N':
					xmod = -1000000000;
					zmod = 0;
					break;

				case 's':
				case 'S':
					xmod = 1000000000;
					zmod = 0;
					break;

				case 'e':
				case 'E':
					xmod = 0;
					zmod = -1000000000;
					break;

				case 'w':
				case 'W':
					xmod = 0;
					zmod = 1000000000;
					break;

				default:
					throw new FoxBukkitCommandException("Unrecognised parameter");
				}

				location = new Location(ply.getWorld(), xmod, 0, zmod);
				break;

			default:
				Location loc = null;
				for (BlockFace face : BlockFace.values()) {
					if (!face.name().replaceAll("_", "").equalsIgnoreCase(args[0]))
						continue;

					loc = new Location(ply.getWorld(), face.getModX()*1000000000, face.getModY()*1000000000, face.getModZ()*1000000000);
					break;
				}

				if (loc == null)
					throw new FoxBukkitCommandException("Unrecognised parameter");
				location = loc;
			}
			break;
		}

		ply.setCompassTarget(location);

		PlayerHelper.sendDirectedMessage(ply, String.format("Set your compass target to %d/%d/%d", location.getBlockX(), location.getBlockY(), location.getBlockZ()));
	}

	private void addCompassTarget(Player ply, Player target) {
		playerCompassTargets.put(ply, target);

		if (taskId != -1)
			return;

		taskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (Entry<Player, Player> entry : playerCompassTargets.entrySet()) {
					entry.getKey().setCompassTarget(entry.getValue().getLocation());
				}
			}
		}, 0, 1);
	}

	private void removeCompassTarget(Player ply) {
		playerCompassTargets.remove(ply);
		if (!playerCompassTargets.isEmpty())
			return;

		if (taskId == -1)
			return;

		plugin.getServer().getScheduler().cancelTask(taskId);
		taskId = -1;
	}
}
