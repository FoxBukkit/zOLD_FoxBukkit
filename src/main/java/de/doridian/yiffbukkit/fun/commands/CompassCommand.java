package de.doridian.yiffbukkit.fun.commands;

import de.doridian.yiffbukkit.main.PermissionDeniedException;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.ICommand;
import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkit.warp.WarpDescriptor;
import de.doridian.yiffbukkit.main.commands.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.ICommand.Usage;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;

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
@Permission("yiffbukkit.compass")
public class CompassCommand extends ICommand {
	int taskId = -1;
	protected Map<Player, Player> playerCompassTargets = new HashMap<Player, Player>();
	{
		plugin.playerHelper.registerMap(playerCompassTargets);
	}

	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		if (args.length == 0) {
			final float yaw = ply.getLocation().getYaw();
			PlayerHelper.sendDirectedMessage(ply, "Direction: "+ Utils.yawToDirection(yaw)+" ("+Math.round((yaw+720)%360)+")");
			return;
		}

		if ("player".equals(args[0]) || "pl".equals(args[0])) {
			if (!ply.hasPermission("yiffbukkit.compass.player"))
				throw new PermissionDeniedException();

			if (args.length < 2)
				throw new YiffBukkitCommandException("Expected player name");

			final Player target = playerHelper.matchPlayerSingle(args[1]);

			if (!playerHelper.canTp(ply, target))
				throw new PermissionDeniedException();

			playerCompassTargets.put(ply, target);

			if (taskId == -1) {
				taskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() { public void run() {
					for (Entry<Player, Player> entry : playerCompassTargets.entrySet()) {
						entry.getKey().setCompassTarget(entry.getValue().getLocation());
					}
				}}, 0, 1);
			}

			PlayerHelper.sendDirectedMessage(ply, "Set your compass to follow "+target.getName()+".");
			return;
		}

		playerCompassTargets.remove(ply);
		if (playerCompassTargets.isEmpty()) {
			plugin.getServer().getScheduler().cancelTask(taskId);
			taskId = -1;
		}

		final Location location;
		if ("spawn".equals(args[0])) {
			location = ply.getWorld().getSpawnLocation();
		}
		else if ("home".equals(args[0])) {
			location = playerHelper.getPlayerHomePosition(ply, "default");
		}
		else if ("here".equals(args[0])) {
			location = ply.getLocation();
		}
		else if ("warp".equals(args[0])) {
			if (args.length < 2)
				throw new YiffBukkitCommandException("Expected warp name");

			final WarpDescriptor warpDescriptor = plugin.warpEngine.getWarp(ply, args[1]);

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

		PlayerHelper.sendDirectedMessage(ply, String.format("Set your compass target to %d/%d/%d", location.getBlockX(), location.getBlockY(), location.getBlockZ()));
	}
}
