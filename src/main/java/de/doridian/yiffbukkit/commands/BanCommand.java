package de.doridian.yiffbukkit.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		args = parseFlags(args);

		Matcher matcher = Pattern.compile("^\"(.*)\"$").matcher(args[0]);

		final Player otherply = matcher.matches() ? new OfflinePlayer(plugin.getServer(), ply.getWorld(), matcher.group(1)) : playerHelper.MatchPlayerSingle(args[0]);

		if (booleanFlags.contains('j')) {
			try {
				plugin.jailEngine.jailPlayer(otherply, false);
			} catch (JailException e) { }
		}

		if (booleanFlags.contains('r')) {
			ply.chat("/bb rollback "+otherply.getName());

			if (booleanFlags.contains('c')) {
				ply.chat("/bb confirm");
			}
		}

		String reason = Utils.concatArray(args, 1, "Kickbanned by " + ply.getName());

		if(playerHelper.GetPlayerLevel(ply) <= playerHelper.GetPlayerLevel(otherply))
			throw new PermissionDeniedException();

		playerHelper.SetPlayerRank(otherply.getName(), "banned");
		if (matcher.matches()) {
			playerHelper.SendServerMessage(ply.getName() + " banned " + otherply.getName() + " (reason: "+reason+")");
		}
		else {
			otherply.kickPlayer(reason);
			playerHelper.SendServerMessage(ply.getName() + " kickbanned " + otherply.getName() + " (reason: "+reason+")");
		}
	}
}
