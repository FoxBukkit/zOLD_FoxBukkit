package de.doridian.yiffbukkit.vanish;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class VanishPlayerListener extends PlayerListener {
	final Vanish vanish;

	public VanishPlayerListener(Vanish vanish) {
		this.vanish = vanish;

		vanish.plugin.getServer().getPluginManager().registerEvent(Type.PLAYER_PICKUP_ITEM, this, Priority.Highest, vanish.plugin);

		vanish.plugin.getServer().getPluginManager().registerEvent(Type.PLAYER_JOIN, this, Priority.Monitor, vanish.plugin);

		vanish.plugin.getServer().getPluginManager().registerEvent(Type.PLAYER_QUIT, this, Priority.Monitor, vanish.plugin);
		vanish.plugin.getServer().getPluginManager().registerEvent(Type.PLAYER_KICK, this, Priority.Monitor, vanish.plugin);
	}

	@Override
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if (vanish.isVanished(event.getPlayer()))
			event.setCancelled(true);
	}

	@Override
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player ply = event.getPlayer();

		if (vanish.isVanished(ply))
			vanish.vanishId(ply.getEntityId());
	}

	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
		vanish.unVanishId(event.getPlayer().getEntityId());
	}

	@Override
	public void onPlayerKick(PlayerKickEvent event) {
		if (event.isCancelled())
			return;

		vanish.unVanishId(event.getPlayer().getEntityId());
	}
}
