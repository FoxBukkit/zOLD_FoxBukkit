package de.doridian.yiffbukkit.commands;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.PermissionDeniedException;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.util.Utils;
import de.doridian.yiffbukkit.warp.WarpDescriptor;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("compass")
@Help(
		"Gives the current bearing or sets the compass target.\n" +
		"Direction can be a direction acronym, like N or NE,\n" +
		"or a full direction name, like north_east or northeast."
)
@Usage("[spawn|home|here|player <name>|warp <name>|<direction>]")
@Permission("yiffbukkit.compass")
public class CompassCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		if (args.length == 0) {
			float yaw = ply.getLocation().getYaw();
			playerHelper.sendDirectedMessage(ply, "Direction: "+Utils.yawToDirection(yaw)+" ("+Math.round((yaw+720)%360)+")");
			return;
		}

		final Location location;
		if (args[0].equals("spawn")) {
			location = ply.getWorld().getSpawnLocation();
		}
		else if (args[0].equals("home")) {
			location = playerHelper.getPlayerHomePosition(ply);
		}
		else if (args[0].equals("here")) {
			location = ply.getLocation();
		}
		else if (args[0].equals("player")) {
			if (args.length < 2)
				throw new YiffBukkitCommandException("Expected player name");

			Player target = playerHelper.matchPlayerSingle(args[1]);

			if (!playerHelper.canTp(ply, target))
				throw new PermissionDeniedException();

			location = target.getLocation();
		}
		else if (args[0].equals("warp")) {
			if (args.length < 2)
				throw new YiffBukkitCommandException("Expected warp name");

			WarpDescriptor warpDescriptor = plugin.warpEngine.getWarp(ply.getName(), args[1]);

			location = warpDescriptor.location;
		}
		else {
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
					throw new YiffBukkitCommandException("Unrecognised parameter");
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
					throw new YiffBukkitCommandException("Unrecognised parameter");
				}

				location = new Location(ply.getWorld(), xmod, 0, zmod);
				/* FALL-THROUGH */
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
					throw new YiffBukkitCommandException("Unrecognised parameter");
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
					throw new YiffBukkitCommandException("Unrecognised parameter");
				location = loc;
			}		
		}

		ply.setCompassTarget(location);

		playerHelper.sendDirectedMessage(ply, String.format("Set your compass target to %d/%d/%d", location.getBlockX(), location.getBlockY(), location.getBlockZ()));
	}
}
