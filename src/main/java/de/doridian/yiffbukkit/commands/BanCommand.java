package de.doridian.yiffbukkit.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.firestar.mcbans.mcbans;

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
		"  -r to rollback\n"+
		"  -g to issue an mcbans.com global ban"
)
@Usage("[<flags>] <name> [reason here]")
@Level(3)
@BooleanFlags("jrgc")
public class BanCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		args = parseFlags(args);

		Matcher matcher = Pattern.compile("^\"(.*)\"$").matcher(args[0]);

		final Player otherply;
		if (matcher.matches())
			otherply = new OfflinePlayer(plugin.getServer(), plugin.getOrCreateWorld("world", Environment.NORMAL), matcher.group(1));
		else
			otherply = playerHelper.matchPlayerSingle(args[0]);

		if(playerHelper.getPlayerLevel(commandSender) <= playerHelper.getPlayerLevel(otherply))
			throw new PermissionDeniedException();

		if (booleanFlags.contains('j')) {
			try {
				plugin.jailEngine.jailPlayer(otherply, false);
			}
			catch (JailException e) { }
		}

		playerHelper.setPlayerRank(otherply.getName(), "banned");

		if(booleanFlags.contains('g') || booleanFlags.contains('r')) {
			asPlayer(commandSender).chat("/lb writelogfile player "+otherply.getName());
		}

		String reason = Utils.concatArray(args, 1, "Kickbanned by " + commandSender.getName());

		mcbans mcbansPlugin = (mcbans) plugin.getServer().getPluginManager().getPlugin("mcbans");
		if (booleanFlags.contains('g')) {
			mcbansPlugin.mcb_handler.ban(otherply.getName(), commandSender.getName(), reason, "g");
		}
		else {
			mcbansPlugin.mcb_handler.ban(otherply.getName(), commandSender.getName(), reason, "l");
		}

		if (booleanFlags.contains('r')) {
			asPlayer(commandSender).chat("/lb rollback player "+otherply.getName());

			if (booleanFlags.contains('c')) {
				playerHelper.sendDirectedMessage(commandSender, "The -c flag has no effect now. It is no longer needed with LogBlock.");
			}
		}

		if (matcher.matches()) {
			playerHelper.sendServerMessage(commandSender.getName() + " banned " + otherply.getName() + " (reason: "+reason+")");
		}
		else {
			otherply.kickPlayer(reason);
			playerHelper.sendServerMessage(commandSender.getName() + " kickbanned " + otherply.getName() + " (reason: "+reason+")");
		}
	}
}
