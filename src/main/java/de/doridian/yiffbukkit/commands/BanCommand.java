package de.doridian.yiffbukkit.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.PermissionDeniedException;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.util.Utils;
import de.doridian.yiffbukkit.commands.ICommand.*;
import de.doridian.yiffbukkit.jail.JailException;
import de.doridian.yiffbukkit.offlinebukkit.OfflinePlayer;

@Names("ban")
@Help(
		"Bans specified user. Specify offline players in quotation marks.\n"+
		"Flags:\n"+
		"  -j to unjail the player first\n"+
		"  -r to rollback (add -c to instantly confirm)"
)
@Usage("[<flags>] <name> [reason here]")
@Level(3)
@BooleanFlags("jrc")
public class BanCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		args = parseFlags(args);

		Matcher matcher = Pattern.compile("^\"(.*)\"$").matcher(args[0]);

		final Player otherply;
		if (matcher.matches())
			otherply = new OfflinePlayer(plugin.getServer(), plugin.GetOrCreateWorld("world", Environment.NORMAL), matcher.group(1));
		else
			otherply = playerHelper.MatchPlayerSingle(args[0]);

		if(playerHelper.GetPlayerLevel(commandSender) <= playerHelper.GetPlayerLevel(otherply))
			throw new PermissionDeniedException();

		if (booleanFlags.contains('j')) {
			try {
				plugin.jailEngine.jailPlayer(otherply, false);
			}
			catch (JailException e) { }
		}

		playerHelper.SetPlayerRank(otherply.getName(), "banned");

		if (booleanFlags.contains('r')) {
			asPlayer(commandSender).chat("/bb rollback "+otherply.getName());

			if (booleanFlags.contains('c')) {
				asPlayer(commandSender).chat("/bb confirm");
			}
		}

		String reason = Utils.concatArray(args, 1, "Kickbanned by " + commandSender.getName());

		if (matcher.matches()) {
			playerHelper.SendServerMessage(commandSender.getName() + " banned " + otherply.getName() + " (reason: "+reason+")");
		}
		else {
			otherply.kickPlayer(reason);
			playerHelper.SendServerMessage(commandSender.getName() + " kickbanned " + otherply.getName() + " (reason: "+reason+")");
		}
	}
}
