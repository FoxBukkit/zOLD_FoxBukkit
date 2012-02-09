package de.doridian.yiffbukkit.main.commands;

import de.doridian.yiffbukkit.delme.FakePermissions;
import de.doridian.yiffbukkit.main.PermissionDeniedException;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.ICommand.Usage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Set;

@Names("god")
@Help("Activates or deactivates god mode.")
@Usage("[<name>] [on|off]")
@Permission("yiffbukkitsplit.players.god")
public class GodCommand extends AbstractPlayerStateCommand implements Listener {
	private final Set<String> godded = states;

	public GodCommand() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		Player ply = (Player)event.getEntity();

		String playerName = ply.getName();

		if (godded.contains(playerName))
			event.setCancelled(true);
	}

	@Override
	protected void onStateChange(boolean prevState, boolean newState, String targetName, CommandSender commandSender) throws YiffBukkitCommandException {
		if (commandSender.getName().equals(targetName)) {
			/*if (!FakePermissions.has(commandSender, "yiffbukkitsplit.players.god.self"))
				throw new PermissionDeniedException();*/
		}
		else {
			if (!FakePermissions.has(commandSender, "yiffbukkitsplit.players.god.others"))
				throw new PermissionDeniedException();
		}

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
