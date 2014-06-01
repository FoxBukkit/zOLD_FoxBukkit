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
package com.foxelbox.foxbukkit.bans.commands;

import com.foxelbox.foxbukkit.bans.Bans.BanType;
import com.foxelbox.foxbukkit.core.FoxBukkit;
import com.foxelbox.foxbukkit.core.util.PlayerHelper;
import com.foxelbox.foxbukkit.jail.JailComponent;
import com.foxelbox.foxbukkit.jail.JailException;
import com.foxelbox.foxbukkit.main.FoxBukkitCommandException;
import com.foxelbox.foxbukkit.main.PermissionDeniedException;
import com.foxelbox.foxbukkit.main.commands.system.ICommand;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.*;
import com.foxelbox.foxbukkit.main.util.Utils;
import com.foxelbox.foxbukkit.permissions.FoxBukkitPermissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Names("ban")
@Help(
		"Bans specified user. Specify offline players in quotation marks.\n"+
		"Flags:\n"+
		"  -j to unjail the player first\n"+
		"  -r to rollback\n"+
		"  -g to issue an bans.com global ban\n"+
		"  -t <time> to issue a temporary ban. Possible suffixes:\n"+
		"       m=minutes, h=hours, d=days"
)
@Usage("[<flags>] <name> [reason here]")
@BooleanFlags("jrg")
@StringFlags("t")
@Permission("foxbukkit.users.ban")
@AbusePotential
public class BanCommand extends ICommand {
	private static final JailComponent jail = (JailComponent) FoxBukkit.instance.componentSystem.getComponent("jail");

	@Override
	public void run(CommandSender commandSender, String[] args, String argStr, String commandName) throws FoxBukkitCommandException {
		args = parseFlags(args);
		executeBan(commandSender, args[0], Utils.concatArray(args, 1, null), plugin, booleanFlags.contains('j'), booleanFlags.contains('r'), booleanFlags.contains('g'), stringFlags.get('t'));
	}

	public static void executeBan(CommandSender commandSender, String plyName, String reason, FoxBukkit plugin, boolean unjail, boolean rollback, boolean global, final String duration) throws FoxBukkitCommandException {
		if (!commandSender.hasPermission("foxbukkit.users.ban")) throw new PermissionDeniedException();

		final Player otherply = plugin.playerHelper.matchPlayerSingle(plyName, false);

		if (PlayerHelper.getPlayerLevel(commandSender) <= PlayerHelper.getPlayerLevel(otherply))
			throw new PermissionDeniedException();

		if (unjail) {
			try {
				jail.engine.jailPlayer(otherply, false);
			}
			catch (JailException e) {
				PlayerHelper.sendDirectedMessage(commandSender, e.getMessage(), e.getColor());
			}
		}

		FoxBukkitPermissions.removeCOPlayer(otherply);

		if (global || rollback) {
			asPlayer(commandSender).chat("/lb writelogfile player "+otherply.getName());
		}

		if (reason == null) {
			reason = "Kickbanned by " + commandSender.getName();
		}

		final BanType type;
		if (duration != null) {
			if (global)
				throw new FoxBukkitCommandException("Bans can only be either global or temporary");
			type = BanType.TEMPORARY;

			if (duration.length() < 2)
				throw new FoxBukkitCommandException("Malformed ban duration");

			final String measure = duration.substring(duration.length() - 1);

			final long durationValue;
			try {
				durationValue = Long.parseLong(duration.substring(0, duration.length() - 2).trim());
			}
			catch (NumberFormatException e) {
				throw new FoxBukkitCommandException("Malformed ban duration");
			}

			plugin.bans.ban(commandSender, otherply, reason, type, durationValue, measure);
		}
		else {
			if (global) {
				type = BanType.GLOBAL;
			} else {
				type = BanType.LOCAL;
			}

			plugin.bans.ban(commandSender, otherply, reason, type);
		}

		if (rollback) {
			asPlayer(commandSender).chat("/lb rollback player "+otherply.getName());
		}

		KickCommand.kickPlayer(otherply, reason);
		FoxBukkitPermissions.removeCOPlayer(otherply);
	}
}
