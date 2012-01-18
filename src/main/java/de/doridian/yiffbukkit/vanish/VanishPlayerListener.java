package de.doridian.yiffbukkit.vanish;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class VanishPlayerListener implements Listener {
	final Vanish vanish;

	public VanishPlayerListener(Vanish vanish) {
		this.vanish = vanish;

		vanish.plugin.getServer().getPluginManager().registerEvents(this, vanish.plugin);
	}

	@EventHandler(event = PlayerPickupItemEvent.class, priority = EventPriority.HIGHEST)
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if (vanish.isVanished(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler(event = PlayerJoinEvent.class, priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player ply = event.getPlayer();

		if (vanish.isVanished(ply))
			vanish.vanishId(ply.getEntityId());
	}

	@EventHandler(event = PlayerQuitEvent.class, priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		vanish.unVanishId(event.getPlayer().getEntityId());
	}

	@EventHandler(event = PlayerKickEvent.class, priority = EventPriority.MONITOR)
	public void onPlayerKick(PlayerKickEvent event) {
		if (event.isCancelled())
			return;

		vanish.unVanishId(event.getPlayer().getEntityId());
	}
}
