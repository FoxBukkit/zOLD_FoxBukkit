package de.doridian.yiffbukkit.bans.commands;

import de.doridian.yiffbukkit.bans.Ban;
import de.doridian.yiffbukkit.bans.BanResolver;
import de.doridian.yiffbukkit.bans.listeners.BansPlayerListener;
import de.doridian.yiffbukkit.core.util.PlayerHelper;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
					}
				});
			}
		}.start();
	}
}
