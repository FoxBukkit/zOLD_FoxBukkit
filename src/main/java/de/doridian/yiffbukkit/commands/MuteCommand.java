package de.doridian.yiffbukkit.commands;

import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.PluginManager;

import de.doridian.yiffbukkit.PermissionDeniedException;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("mute")
@Help("Mutes or unmutes a player.")
@Usage("<name> [on|off]")
@Level(4)
public class MuteCommand extends AbstractPlayerStateCommand {
	private final Set<String> muted = states;

	public MuteCommand() {
		final PlayerListener chatListener = new PlayerListener() {
			@Override
			public void onPlayerChat(PlayerChatEvent event) {
				if (muted.contains(event.getPlayer().getName())) {
					plugin.playerHelper.SendDirectedMessage(event.getPlayer(), "You are muted and cannot speak at this time.");
					event.setCancelled(true);
					return;
				}
			}

			@Override
			public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
				if (muted.contains(event.getPlayer().getName())) {
					plugin.playerHelper.SendDirectedMessage(event.getPlayer(), "You are muted and cannot use commands at this time.");
					event.setCancelled(true);
					return;
				}
			}
		};

		PluginManager pm = plugin.getServer().getPluginManager();
		pm.registerEvent(Type.PLAYER_CHAT, chatListener, Priority.Highest, plugin);
		pm.registerEvent(Type.PLAYER_COMMAND_PREPROCESS, chatListener, Priority.Lowest, plugin);
	}

	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		if (args.length == 0)
			throw new YiffBukkitCommandException("Syntax error");

		super.Run(ply, args, argStr);
	}

	@Override
	protected void onStateChange(boolean prevState, boolean newState, String targetName, Player commandSender) throws YiffBukkitCommandException {
		final String commandSenderName = commandSender.getName();
		final Player target = plugin.getServer().getPlayer(targetName);

		if (targetName.equals(commandSenderName))
			throw new YiffBukkitCommandException("You cannot mute yourself");

		final Integer commandSenderLevel = playerHelper.GetPlayerLevel(commandSender);
		final Integer targetLevel = playerHelper.GetPlayerLevel(target);
		if (commandSenderLevel <= targetLevel)
			throw new PermissionDeniedException();

		if (commandSenderLevel < 5 && targetLevel > 0)
			throw new PermissionDeniedException();

		if (targetName.equals(commandSenderName)) {
			if (newState) {
				if (prevState)
					playerHelper.SendDirectedMessage(commandSender, "You are already muted.");
				else {
					playerHelper.SendServerMessage(commandSenderName+" muted themselves.", commandSender);
					playerHelper.SendDirectedMessage(commandSender, "You are now muted.");
				}
			}
			else {
				if (prevState) {
					playerHelper.SendServerMessage(commandSenderName+" unmuted themselves.", commandSender);
					playerHelper.SendDirectedMessage(commandSender, "You are no longer muted.");
				}
				else
					playerHelper.SendDirectedMessage(commandSender, "You are not muted.");
			}
		}
		else {
			if (newState) {
				if (prevState)
					playerHelper.SendDirectedMessage(commandSender, targetName+" is already muted.");
				else {
					playerHelper.SendServerMessage(commandSenderName+" muted "+targetName+".", commandSender, target);
					playerHelper.SendDirectedMessage(commandSender, "You muted "+targetName+".");
					if (target != null)
						playerHelper.SendDirectedMessage(target, commandSenderName+" muted you.");
				}
			}
			else {
				if (prevState) {
					playerHelper.SendServerMessage(commandSenderName+" unmuted "+targetName+".", commandSender, target);
					playerHelper.SendDirectedMessage(commandSender, "You unmuted "+targetName+".");
					if (target != null)
						playerHelper.SendDirectedMessage(target, commandSenderName+" unmuted you.");
				}
				else
					playerHelper.SendDirectedMessage(commandSender, targetName+" is not muted.");
			}
		}
	}
}