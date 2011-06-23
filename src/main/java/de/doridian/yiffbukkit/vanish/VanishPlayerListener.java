package de.doridian.yiffbukkit.vanish;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class VanishPlayerListener extends PlayerListener {
	final Vanish vanish;
	
	public VanishPlayerListener(Vanish vanish) {
		this.vanish = vanish;
	}

	@Override
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		final Player player = event.getPlayer();
		final String playerName = player.getName();
		if (vanish.vanishedPlayers.contains(playerName))
			event.setCancelled(true);
	}
}
