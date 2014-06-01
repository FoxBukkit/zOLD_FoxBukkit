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
import de.doridian.foxbukkit.main.PermissionDeniedException;
import de.doridian.foxbukkit.main.commands.system.ICommand;
import de.doridian.foxbukkit.main.commands.system.ICommand.Help;
import de.doridian.foxbukkit.main.commands.system.ICommand.Names;
import de.doridian.foxbukkit.main.commands.system.ICommand.Permission;
import de.doridian.foxbukkit.main.commands.system.ICommand.Usage;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@Names("banish")
@Help("Banishes the specified user to the spawn and optionally resets their home location.")
@Usage("<name> [resethome]")
@Permission("foxbukkit.teleport.banish")
public class BanishCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr, String commandName) throws FoxBukkitCommandException {
		boolean resetHome = args.length >= 2 && (args[1].equals("resethome") || args[1].equals("sethome") || args[1].equals("withhome"));

		Player otherply = playerHelper.matchPlayerSingle(args[0]);

		int level = PlayerHelper.getPlayerLevel(commandSender);
		int otherlevel = PlayerHelper.getPlayerLevel(otherply);

		// Players with the same levels can banish each other, but not reset each other's homes
		if (level < otherlevel || (level == otherlevel && resetHome))
			throw new PermissionDeniedException();

		Vector previousPos = otherply.getLocation().toVector();
		final Location teleportTarget = playerHelper.getPlayerSpawnPosition(otherply);
		otherply.teleport(teleportTarget);

		if (resetHome) {
			playerHelper.clearPlayerHomePositionsAndTeleportHistory(otherply);
		}
		else {
			Vector homePos = playerHelper.getPlayerHomePosition(otherply, "default").toVector();

			final long unitsFromPrevious = Math.round(homePos.distance(previousPos));
			String unitsFromYou = "";
			try {
				unitsFromYou = Math.round(homePos.distance(getCommandSenderLocation(commandSender, false).toVector())) + "m from you and ";
			} catch (FoxBukkitCommandException e) { }
			final long unitsFromSpawn = Math.round(homePos.distance(teleportTarget.toVector()));

			PlayerHelper.sendDirectedMessage(
					commandSender, otherply.getName() + "'s home is " +
					unitsFromPrevious + "m from the previous location, " +
					unitsFromYou +
					unitsFromSpawn + "m from the spawn. Use '/banish " + otherply.getName() + " resethome' to move it to the spawn.");
		}

		PlayerHelper.sendServerMessage(commandSender.getName() + " banished " + otherply.getName() + (resetHome ? " and cleared teleport history and home position!" : "!"));
	}
}
