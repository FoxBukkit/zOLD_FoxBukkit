package de.doridian.yiffbukkit.chat.commands;

import de.doridian.yiffbukkit.core.util.PlayerHelper;
import de.doridian.yiffbukkit.main.PermissionDeniedException;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.AbstractPlayerStateCommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;
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
@Permission("yiffbukkit.users.mute")
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
			return;
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if (muted.contains(event.getPlayer().getName())) {
			PlayerHelper.sendDirectedMessage(event.getPlayer(), "You are muted and cannot use commands at this time.");
			event.setCancelled(true);
			return;
		}
	}

	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		if (args.length == 0)
			throw new YiffBukkitCommandException("Syntax error");

		super.Run(ply, args, argStr);
	}

	@Override
	protected void onStateChange(boolean prevState, boolean newState, String targetName, CommandSender commandSender) throws YiffBukkitCommandException {
		final String commandSenderName = commandSender.getName();
		final Player target = plugin.getServer().getPlayer(targetName);

		if (targetName.equals(commandSenderName))
			throw new YiffBukkitCommandException("You cannot mute yourself");

		final Integer commandSenderLevel = playerHelper.getPlayerLevel(commandSender);
		final Integer targetLevel = playerHelper.getPlayerLevel(targetName);
		if (commandSenderLevel <= targetLevel)
			throw new PermissionDeniedException();

		if (!commandSender.hasPermission("yiffbukkit.users.mute.nonguests") && targetLevel > 0)
			throw new PermissionDeniedException();

		if (targetName.equals(commandSenderName)) {
			if (newState) {
				if (prevState)
					PlayerHelper.sendDirectedMessage(commandSender, "You are already muted.");
				else {
					playerHelper.sendServerMessage(commandSenderName+" muted themselves.", commandSender);
					PlayerHelper.sendDirectedMessage(commandSender, "You are now muted.");
				}
			}
			else {
				if (prevState) {
					playerHelper.sendServerMessage(commandSenderName+" unmuted themselves.", commandSender);
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
					playerHelper.sendServerMessage(commandSenderName+" muted "+targetName+".", commandSender, target);
					PlayerHelper.sendDirectedMessage(commandSender, "You muted "+targetName+".");
					if (target != null)
						PlayerHelper.sendDirectedMessage(target, commandSenderName+" muted you.");
				}
			}
			else {
				if (prevState) {
					playerHelper.sendServerMessage(commandSenderName+" unmuted "+targetName+".", commandSender, target);
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