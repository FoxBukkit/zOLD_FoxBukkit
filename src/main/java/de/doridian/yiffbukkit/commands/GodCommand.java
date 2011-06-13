package de.doridian.yiffbukkit.commands;

import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.plugin.PluginManager;

import de.doridian.yiffbukkit.PermissionDeniedException;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("god")
@Help("Activates or deactivates god mode.")
@Usage("[<name>] [on|off]")
@Level(3)
public class GodCommand extends AbstractPlayerStateCommand {
	private final Set<String> godded = states;

	public GodCommand() {
		EntityListener entityListener = new EntityListener() {
			@Override
			public void onEntityDamage(EntityDamageEvent event) {
				if (!(event.getEntity() instanceof Player))
					return;

				Player ply = (Player)event.getEntity();

				String playerName = ply.getName();

				if (godded.contains(playerName))
					event.setCancelled(true);
			}
		};

		PluginManager pm = plugin.getServer().getPluginManager();
		pm.registerEvent(Type.ENTITY_DAMAGE, entityListener, Priority.Highest, plugin);
	}

	@Override
	protected void onStateChange(boolean prevState, boolean newState, String targetName, CommandSender commandSender) throws YiffBukkitCommandException {
		if (playerHelper.getPlayerLevel(commandSender) < 4 && !commandSender.getName().equals(targetName))
			throw new PermissionDeniedException();

		final String commandSenderName = commandSender.getName();
		final Player target = plugin.getServer().getPlayer(targetName);

		if (targetName.equals(commandSenderName)) {
			if (newState) {
				if (prevState)
					playerHelper.sendDirectedMessage(commandSender, "You are already invincible.");
				else {
					playerHelper.sendServerMessage(commandSenderName+" made themselves invincible.", commandSender);
					playerHelper.sendDirectedMessage(commandSender, "You are now invincible.");
				}
			}
			else {
				if (prevState) {
					playerHelper.sendServerMessage(commandSenderName+" made themselves no longer invincible.", commandSender);
					playerHelper.sendDirectedMessage(commandSender, "You are no longer invincible.");
				}
				else
					playerHelper.sendDirectedMessage(commandSender, "You are not invincible.");
			}
		}
		else {
			if (newState) {
				if (prevState)
					playerHelper.sendDirectedMessage(commandSender, targetName+" is already invincible.");
				else {
					playerHelper.sendServerMessage(commandSenderName+" made "+targetName+" invincible.", commandSender, target);
					playerHelper.sendDirectedMessage(commandSender, "You made "+targetName+" invincible.");
					if (target != null)
						playerHelper.sendDirectedMessage(target, commandSenderName+" made you invincible.");
				}
			}
			else {
				if (prevState) {
					playerHelper.sendServerMessage(commandSenderName+" made "+targetName+" no longer invincible.", commandSender, target);
					playerHelper.sendDirectedMessage(commandSender, "You made "+targetName+" no longer invincible.");
					if (target != null)
						playerHelper.sendDirectedMessage(target, commandSenderName+" made you no longer invincible.");
				}
				else
					playerHelper.sendDirectedMessage(commandSender, targetName+" is not invincible.");
			}
		}
	}
}
