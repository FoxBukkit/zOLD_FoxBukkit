package de.doridian.yiffbukkit.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.PermissionDeniedException;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.util.Utils;
import de.doridian.yiffbukkit.commands.ICommand.*;
import de.doridian.yiffbukkit.jail.JailException;
import de.doridian.yiffbukkit.mcbans.MCBans.BanType;

@Names("ban")
@Help(
		"Bans specified user. Specify offline players in quotation marks.\n"+
		"Flags:\n"+
		"  -j to unjail the player first\n"+
		"  -r to rollback\n"+
		"  -g to issue an mcbans.com global ban\n"+
		"  -t <time> to issue a temporary ban. Possible suffixes:\n"+
		"       m=minutes, h=hours, d=days"
)
@Usage("[<flags>] <name> [reason here]")
@BooleanFlags("jrg")
@StringFlags("t")
@Permission("yiffbukkit.users.ban")
public class BanCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		args = parseFlags(args);

		final Player otherply = playerHelper.matchPlayerSingle(args[0], false);

		if(playerHelper.getPlayerLevel(commandSender) <= playerHelper.getPlayerLevel(otherply))
			throw new PermissionDeniedException();

		if (booleanFlags.contains('j')) {
			try {
				plugin.jailEngine.jailPlayer(otherply, false);
			}
			catch (JailException e) { }
		}

		if(booleanFlags.contains('g') || booleanFlags.contains('r')) {
			asPlayer(commandSender).chat("/lb writelogfile player "+otherply.getName());
		}

		final String reason = Utils.concatArray(args, 1, "Kickbanned by " + commandSender.getName());

		final BanType type;
		if (stringFlags.containsKey('t')) {
			if (booleanFlags.contains('g'))
				throw new YiffBukkitCommandException("Bans can only be either global or temporary");
			type = BanType.TEMPORARY;

			final String duration = stringFlags.get('t');
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

			plugin.mcbans.ban(commandSender, otherply, reason, type, durationValue, measure);
		}
		else {
			if (booleanFlags.contains('g')) {
				type = BanType.GLOBAL;
			} else {
				type = BanType.LOCAL;
			}

			plugin.mcbans.ban(commandSender, otherply, reason, type);
		}

		if (booleanFlags.contains('r')) {
			asPlayer(commandSender).chat("/lb rollback player "+otherply.getName());
		}

		otherply.kickPlayer(reason);
	}
}
