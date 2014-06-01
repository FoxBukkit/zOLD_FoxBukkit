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
package de.doridian.foxbukkit.permissions.commands;

import de.doridian.foxbukkit.chat.RedisHandler;
import de.doridian.foxbukkit.core.util.PlayerHelper;
import de.doridian.foxbukkit.main.FoxBukkitCommandException;
import de.doridian.foxbukkit.main.commands.system.ICommand;
import de.doridian.foxbukkit.main.commands.system.ICommand.Help;
import de.doridian.foxbukkit.main.commands.system.ICommand.Names;
import de.doridian.foxbukkit.main.commands.system.ICommand.Permission;
import de.doridian.foxbukkit.main.commands.system.ICommand.Usage;
import de.doridian.foxbukkit.main.util.Utils;
import de.doridian.foxbukkit.warp.WarpDescriptor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

@Names({ "who" })
@Help("Prints user list if used without parameters or information about the specified user")
@Usage("[name]")
@Permission("foxbukkit.who")
public class WhoCommand extends ICommand {
	@Override
	public void run(final CommandSender commandSender, String[] args, String argStr, String commandName) throws FoxBukkitCommandException {
		if (args.length == 0) {
			RedisHandler.sendMessage(asPlayer(commandSender), "/list");
			return;
		}

		final Location defaultLocation = new Location(plugin.getOrCreateWorld("world", Environment.NORMAL), Double.POSITIVE_INFINITY, 0, 0);
		final Location location = getCommandSenderLocation(commandSender, false, defaultLocation);
		final World world = location.getWorld();
		final Player target = playerHelper.matchPlayerSingle(args[0], false);

		PlayerHelper.sendDirectedMessage(commandSender, "Name: " + target.getName());
		PlayerHelper.sendDirectedMessage(commandSender, "Rank: " + PlayerHelper.getPlayerRank(target));
		PlayerHelper.sendDirectedMessage(commandSender, "NameTag: " + playerHelper.GetFullPlayerName(target));
		PlayerHelper.sendDirectedMessage(commandSender, "World: " + target.getWorld().getName());

		final int playerLevel = PlayerHelper.getPlayerLevel(commandSender);
		if (commandSender.hasPermission("foxbukkit.who.lastlogout")) {
			PlayerHelper.sendDirectedMessage(commandSender, "Last logout: " + Utils.readableDate(PlayerHelper.lastLogout(target)));
		}

		final List<String> distances = new ArrayList<>();

		final Location targetLocation = target.getLocation();
		if (commandSender.hasPermission("foxbukkit.who.position") && playerLevel >= PlayerHelper.getPlayerLevel(target)) {
			PlayerHelper.sendDirectedMessage(commandSender, "Position: " + targetLocation.toVector());

			final Vector offsetFromSpawn = targetLocation.toVector().subtract(world.getSpawnLocation().toVector());
			final long unitsFromSpawn = Math.round(offsetFromSpawn.length());
			final String directionFromSpawn = Utils.yawToDirection(Utils.vectorToYaw(offsetFromSpawn));
			distances.add(unitsFromSpawn+"m "+directionFromSpawn+" from the spawn");

			if (!Double.isInfinite(location.getX())) {
				final Vector offsetFromYou = targetLocation.toVector().subtract(location.toVector());
				final long unitsFromYou = Math.round(offsetFromYou.length());
				final String directionFromYou = Utils.yawToDirection(Utils.vectorToYaw(offsetFromYou));
				distances.add(unitsFromYou+"m "+directionFromYou+" from you");
			}
		}

		if (commandSender.hasPermission("foxbukkit.who.warp") && playerLevel >= PlayerHelper.getPlayerLevel(target)) {
			double minDistance = Double.MAX_VALUE;
			Vector minOffsetFromWarp = null;
			WarpDescriptor closestWarp = null;

			for (WarpDescriptor warpDescriptor : plugin.warpEngine.getWarps().values()) {
				if (!warpDescriptor.location.getWorld().equals(targetLocation.getWorld()))
					continue;

				final Vector currentOffsetFromWarp = targetLocation.toVector().subtract(warpDescriptor.location.toVector());
				final double currentDistance = currentOffsetFromWarp.length();
				if (currentDistance >= minDistance)
					continue;

				minDistance = currentDistance;
				minOffsetFromWarp = currentOffsetFromWarp;
				closestWarp = warpDescriptor;
			}

			if (closestWarp != null) {
				final long unitsFromWarp = Math.round(minOffsetFromWarp.length());
				final String directionFromWarp = Utils.yawToDirection(Utils.vectorToYaw(minOffsetFromWarp));
				distances.add(unitsFromWarp+"m "+directionFromWarp+" from the warp \u00a79"+closestWarp.name+"\u00a7r");
			}
		}

		if (!distances.isEmpty()) {
			final StringBuilder sb = Utils.enumerateStrings(distances);
			sb.insert(0, "That's ");
			sb.append('.');
			PlayerHelper.sendDirectedMessage(commandSender, sb.toString());
		}

		if (commandSender.hasPermission("foxbukkit.who.address") && playerLevel >= PlayerHelper.getPlayerLevel(target) && target.isOnline()) {
			PlayerHelper.sendDirectedMessage(commandSender, "IP: " + PlayerHelper.getPlayerIP(target) + " (" + PlayerHelper.getPlayerHost(target) + ")");
		}
	}
}
