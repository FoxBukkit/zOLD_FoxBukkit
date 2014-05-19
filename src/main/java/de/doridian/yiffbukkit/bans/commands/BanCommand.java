package de.doridian.yiffbukkit.bans.commands;

import de.doridian.yiffbukkit.bans.Bans.BanType;
import de.doridian.yiffbukkit.core.YiffBukkit;
import de.doridian.yiffbukkit.core.util.PlayerHelper;
import de.doridian.yiffbukkit.jail.JailComponent;
import de.doridian.yiffbukkit.jail.JailException;
import de.doridian.yiffbukkit.main.PermissionDeniedException;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.*;
import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkit.permissions.YiffBukkitPermissions;
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
@Permission("yiffbukkit.users.ban")
@AbusePotential
public class BanCommand extends ICommand {
	private static final JailComponent jail = (JailComponent) YiffBukkit.instance.componentSystem.getComponent("jail");

	@Override
	public void run(CommandSender commandSender, String[] args, String argStr, String commandName) throws YiffBukkitCommandException {
		args = parseFlags(args);
		executeBan(commandSender, args[0], Utils.concatArray(args, 1, null), plugin, booleanFlags.contains('j'), booleanFlags.contains('r'), booleanFlags.contains('g'), stringFlags.get('t'));
	}

	public static void executeBan(CommandSender commandSender, String plyName, String reason, YiffBukkit plugin, boolean unjail, boolean rollback, boolean global, final String duration) throws YiffBukkitCommandException {
		if (!commandSender.hasPermission("yiffbukkit.users.ban")) throw new PermissionDeniedException();

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

		YiffBukkitPermissions.removeCOPlayer(otherply);

		if (global || rollback) {
			asPlayer(commandSender).chat("/lb writelogfile player "+otherply.getName());
		}

		if (reason == null) {
			reason = "Kickbanned by " + commandSender.getName();
		}

		final BanType type;
		if (duration != null) {
			if (global)
				throw new YiffBukkitCommandException("Bans can only be either global or temporary");
			type = BanType.TEMPORARY;

			if (duration.length() < 2)
				throw new YiffBukkitCommandException("Malformed ban duration");

			final String measure = duration.substring(duration.length() - 1);

			final long durationValue;
			try {
				durationValue = Long.parseLong(duration.substring(0, duration.length() - 2).trim());
			}
			catch (NumberFormatException e) {
				throw new YiffBukkitCommandException("Malformed ban duration");
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
		YiffBukkitPermissions.removeCOPlayer(otherply);
	}
}
