package de.doridian.yiffbukkit.commands;

import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.plugin.PluginManager;

import de.doridian.yiffbukkit.YiffBukkit;

public class GodCommand extends AbstractPlayerStateCommand {
	private final Set<String> godded = states;

	public GodCommand(YiffBukkit plug) {
		super(plug);
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
	public int GetMinLevel() {
		return 4;
	}

	@Override
	protected void displayMessage(boolean prevState, boolean newState, String targetName, Player commandSender) {
		final String commandSenderName = commandSender.getName();

		if (targetName.equals(commandSenderName)) {
			if (newState) {
				if (prevState)
					playerHelper.SendDirectedMessage(commandSender, "You are already invincible.");
				else
					playerHelper.SendDirectedMessage(commandSender, "You are now invincible.");
			}
			else {
				if (prevState)
					playerHelper.SendDirectedMessage(commandSender, "You are no longer invincible.");
				else
					playerHelper.SendDirectedMessage(commandSender, "You are not invincible.");
			}
		}
		else {
			if (newState) {
				if (prevState)
					playerHelper.SendDirectedMessage(commandSender, targetName+" is already invincible.");
				else
					playerHelper.SendServerMessage(commandSenderName+" made "+targetName+" invincible.");
			}
			else {
				if (prevState)
					playerHelper.SendServerMessage(commandSenderName+" made "+targetName+" no longer invincible.");
				else
					playerHelper.SendDirectedMessage(commandSender, targetName+" is not invincible.");
			}
		}
	}

	@Override
	public String GetHelp() {
		return "Activates or deactivates god mode.";
	}
}
