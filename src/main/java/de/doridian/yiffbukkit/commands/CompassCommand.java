package de.doridian.yiffbukkit.commands;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.PermissionDeniedException;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.util.Utils;
import de.doridian.yiffbukkit.warp.WarpDescriptor;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("compass")
@Help("Gives you your current bearing")
@Usage("[spawn|home|here|player <name>|warp <name>]")
@Level(0)
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
			throw new YiffBukkitCommandException("Unrecognised parameter");
		}

		ply.setCompassTarget(new Location(location.getWorld(), location.getX()*16, location.getY()*16, location.getZ()*16));

		playerHelper.sendDirectedMessage(ply, String.format("Set your compass target to %d/%d/%d", location.getBlockX(), location.getBlockY(), location.getBlockZ()));
	}
}
