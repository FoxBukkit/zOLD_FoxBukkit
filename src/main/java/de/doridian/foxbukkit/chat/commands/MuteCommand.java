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
package de.doridian.foxbukkit.chat.commands;

import de.doridian.foxbukkit.core.util.PlayerHelper;
import de.doridian.foxbukkit.main.FoxBukkitCommandException;
import de.doridian.foxbukkit.main.PermissionDeniedException;
import de.doridian.foxbukkit.main.commands.AbstractPlayerStateCommand;
import de.doridian.foxbukkit.main.commands.system.ICommand.Help;
import de.doridian.foxbukkit.main.commands.system.ICommand.Names;
import de.doridian.foxbukkit.main.commands.system.ICommand.Permission;
import de.doridian.foxbukkit.main.commands.system.ICommand.Usage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Set;

@Names("mute")
@Help("Mutes or unmutes a player.")
@Usage("<name> [on|off]")
@Permission("foxbukkit.users.mute")
public class MuteCommand extends AbstractPlayerStateCommand implements Listener {
	private final Set<String> muted = states;

	public MuteCommand() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		if (muted.contains(event.getPlayer().getName())) {
			PlayerHelper.sendDirectedMessage(event.getPlayer(), "You are muted and cannot speak at this time.");
			event.setCancelled(true);
			//noinspection UnnecessaryReturnStatement
			return;
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if (muted.contains(event.getPlayer().getName())) {
			PlayerHelper.sendDirectedMessage(event.getPlayer(), "You are muted and cannot use commands at this time.");
			event.setCancelled(true);
			//noinspection UnnecessaryReturnStatement
			return;
		}
	}

	@Override
	public void Run(Player ply, String[] args, String argStr, String commandName) throws FoxBukkitCommandException {
		if (args.length == 0)
			throw new FoxBukkitCommandException("Syntax error");

		super.Run(ply, args, argStr, commandName);
	}

	@Override
	protected void onStateChange(boolean prevState, boolean newState, String targetName, CommandSender commandSender) throws FoxBukkitCommandException {
		final String commandSenderName = commandSender.getName();
		final Player target = plugin.getServer().getPlayer(targetName);

		if (targetName.equals(commandSenderName))
			throw new FoxBukkitCommandException("You cannot mute yourself");

		final Integer commandSenderLevel = PlayerHelper.getPlayerLevel(commandSender);
		final Integer targetLevel = PlayerHelper.getPlayerLevel(target.getUniqueId());
		if (commandSenderLevel <= targetLevel)
			throw new PermissionDeniedException();

		if (!commandSender.hasPermission("foxbukkit.users.mute.nonguests") && targetLevel > 0)
			throw new PermissionDeniedException();

		if (targetName.equals(commandSenderName)) {
			if (newState) {
				if (prevState)
					PlayerHelper.sendDirectedMessage(commandSender, "You are already muted.");
				else {
					PlayerHelper.sendServerMessage(commandSenderName + " muted themselves.", commandSender);
					PlayerHelper.sendDirectedMessage(commandSender, "You are now muted.");
				}
			}
			else {
				if (prevState) {
					PlayerHelper.sendServerMessage(commandSenderName + " unmuted themselves.", commandSender);
					PlayerHelper.sendDirectedMessage(commandSender, "You are no longer muted.");
				}
				else
					PlayerHelper.sendDirectedMessage(commandSender, "You are not muted.");
			}
		}
		else {
			if (newState) {
				if (prevState)
					PlayerHelper.sendDirectedMessage(commandSender, targetName+" is already muted.");
				else {
					PlayerHelper.sendServerMessage(commandSenderName + " muted " + targetName + ".", commandSender, target);
					PlayerHelper.sendDirectedMessage(commandSender, "You muted "+targetName+".");
					if (target != null)
						PlayerHelper.sendDirectedMessage(target, commandSenderName+" muted you.");
				}
			}
			else {
				if (prevState) {
					PlayerHelper.sendServerMessage(commandSenderName + " unmuted " + targetName + ".", commandSender, target);
					PlayerHelper.sendDirectedMessage(commandSender, "You unmuted "+targetName+".");
					if (target != null)
						PlayerHelper.sendDirectedMessage(target, commandSenderName+" unmuted you.");
				}
				else
					PlayerHelper.sendDirectedMessage(commandSender, targetName+" is not muted.");
			}
		}
	}
}