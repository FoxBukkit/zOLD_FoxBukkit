package de.doridian.yiffbukkit.vanish;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class VanishPlayerListener extends PlayerListener {
	final Vanish vanish;

	public VanishPlayerListener(Vanish vanish) {
		this.vanish = vanish;

		vanish.plugin.getServer().getPluginManager().registerEvent(Type.PLAYER_PICKUP_ITEM, this, Priority.Highest, vanish.plugin);
	}

	@Override
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		final Player player = event.getPlayer();
		final String playerName = player.getName();
		if (vanish.vanishedPlayers.contains(playerName))
			event.setCancelled(true);
	}
}
