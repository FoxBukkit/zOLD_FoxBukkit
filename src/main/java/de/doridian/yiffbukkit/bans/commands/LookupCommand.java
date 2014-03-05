package de.doridian.yiffbukkit.bans.commands;

import de.doridian.yiffbukkit.bans.Ban;
import de.doridian.yiffbukkit.bans.BanResolver;
import de.doridian.yiffbukkit.bans.FishBansResolver;
import de.doridian.yiffbukkit.bans.listeners.BansPlayerListener;
import de.doridian.yiffbukkit.core.util.PlayerHelper;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@ICommand.Names("lookup")
@ICommand.Help("Gets ban and alt information about specified user")
@ICommand.Usage("<name>")
@ICommand.Permission("yiffbukkit.users.lookup")
public class LookupCommand extends ICommand {
	@Override
	public void run(final CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		final Player otherply = plugin.playerHelper.matchPlayerSingle(args[0], false);
		final String user = otherply.getName();
		new Thread() {
			public void run() {
				final Ban ban = BanResolver.getBan(user);
				final String altList = BansPlayerListener.makePossibleAltString(user);
				final HashMap<String, Integer> fishBans = FishBansResolver.getBanCounts(user);

				final StringBuilder fishBansStr = new StringBuilder(user + " has");
				for(Map.Entry<String, Integer> fishBanEntry : fishBans.entrySet())
					if(fishBanEntry.getKey() != null && fishBanEntry.getValue() != null)
						fishBansStr.append(String.format(" %1$d ban(s) on %2$s,", fishBanEntry.getValue(), fishBanEntry.getKey()));
				fishBansStr.deleteCharAt(fishBansStr.length() - 1);

				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					@Override
					public void run() {
						if(ban != null) {
							PlayerHelper.sendDirectedMessage(commandSender, String.format("Player %1$s IS banned by %2$s for the reason of \"%3$s\"", user, ban.getAdmin(), ban.getReason()));
						} else {
							PlayerHelper.sendDirectedMessage(commandSender, String.format("Player %1$s is NOT banned", user));
						}
						if(altList != null) {
							PlayerHelper.sendDirectedMessage(commandSender, altList);
						} else {
							PlayerHelper.sendDirectedMessage(commandSender, String.format("No possible alts of %1$s found", user));
						}
						PlayerHelper.sendDirectedMessage(commandSender, fishBansStr.toString());
					}
				});
			}
		}.start();
	}
}
