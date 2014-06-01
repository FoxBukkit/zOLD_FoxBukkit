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

import com.foxelbox.foxbukkit.bans.Ban;
import com.foxelbox.foxbukkit.bans.BanResolver;
import com.foxelbox.foxbukkit.bans.FishBansResolver;
import com.foxelbox.foxbukkit.bans.listeners.BansPlayerListener;
import com.foxelbox.foxbukkit.core.util.PlayerHelper;
import com.foxelbox.foxbukkit.main.FoxBukkitCommandException;
import com.foxelbox.foxbukkit.main.commands.system.ICommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ICommand.Names("lookup")
@ICommand.Help("Gets ban and alt information about specified user")
@ICommand.Usage("<name>")
@ICommand.Permission("foxbukkit.users.lookup")
public class LookupCommand extends ICommand {
	@Override
	public void run(final CommandSender commandSender, String[] args, String argStr, String commandName) throws FoxBukkitCommandException {
		final Player otherply = plugin.playerHelper.matchPlayerSingle(args[0], false);
		final String user = otherply.getName();
		final UUID uuid = otherply.getUniqueId() != null ? otherply.getUniqueId() : null;
		new Thread() {
			public void run() {
				final Ban ban = BanResolver.getBan(user, uuid);
				final String altList = BansPlayerListener.makePossibleAltString(user, uuid);
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
							PlayerHelper.sendDirectedMessage(commandSender, String.format("Player %1$s IS banned by %2$s for the reason of \"%3$s\"", user, ban.getAdmin().name, ban.getReason()));
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
