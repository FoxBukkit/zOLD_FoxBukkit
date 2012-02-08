package de.doridian.yiffbukkit.mcbans.commands;

import de.doridian.yiffbukkit.main.PermissionDeniedException;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.ICommand;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import de.doridian.yiffbukkit.main.commands.ICommand.BooleanFlags;
import de.doridian.yiffbukkit.main.commands.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.ICommand.StringFlags;
import de.doridian.yiffbukkit.main.commands.ICommand.Usage;
import de.doridian.yiffbukkit.warp.jail.JailException;
import de.doridian.yiffbukkit.mcbans.MCBans.BanType;
import de.doridian.yiffbukkitsplit.util.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
@Permission("yiffbukkitsplit.users.ban")
public class BanCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		args = parseFlags(args);
		executeBan(commandSender, args[0], Utils.concatArray(args, 1, null), plugin, booleanFlags.contains('j'), booleanFlags.contains('r'), booleanFlags.contains('g'), stringFlags.get('t'));
	}

	public static void executeBan(CommandSender commandSender, String plyName, String reason, YiffBukkit plugin, boolean unjail, boolean rollback, boolean global, final String duration) throws YiffBukkitCommandException {
		if(!commandSender.hasPermission("yiffbukkitsplit.users.ban")) throw new PermissionDeniedException();

		final Player otherply = plugin.playerHelper.matchPlayerSingle(plyName, false);

		if(plugin.playerHelper.getPlayerLevel(commandSender) <= plugin.playerHelper.getPlayerLevel(otherply))
			throw new PermissionDeniedException();

		if (unjail) {
			try {
				plugin.jailEngine.jailPlayer(otherply, false);
			}
			catch (JailException e) { }
		}

        boolean saveEvidence = false;

		if(global || rollback) {
			asPlayer(commandSender).chat("/lb writelogfile player "+otherply.getName());
            saveEvidence = true;
		}

		if(reason == null) {
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

			plugin.mcbans.ban(commandSender, otherply, reason, type, durationValue, measure, saveEvidence);
		}
		else {
			if (global) {
				type = BanType.GLOBAL;
			} else {
				type = BanType.LOCAL;
			}

			plugin.mcbans.ban(commandSender, otherply, reason, type, saveEvidence);
		}

		if (rollback) {
			asPlayer(commandSender).chat("/lb rollback player "+otherply.getName());
		}

		otherply.kickPlayer(reason);
	}
}
