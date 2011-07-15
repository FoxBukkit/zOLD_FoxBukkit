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

		vanish.plugin.getServer().getPluginManager().registerEvent(Type.PLAYER_JOIN, this, Priority.Highest, vanish.plugin);

		vanish.plugin.getServer().getPluginManager().registerEvent(Type.PLAYER_QUIT, this, Priority.Highest, vanish.plugin);
		vanish.plugin.getServer().getPluginManager().registerEvent(Type.PLAYER_KICK, this, Priority.Highest, vanish.plugin);
	}

	@Override
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		final Player player = event.getPlayer();
		final String playerName = player.getName();
		if (vanish.vanishedPlayers.contains(playerName))
			event.setCancelled(true);
	}

	@Override
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player ply = event.getPlayer();
		int entityId = ply.getEntityId();

		if (vanish.vanishedPlayers.contains(ply.getName()))
			vanishId(entityId);
	}

	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player ply = event.getPlayer();
		int entityId = ply.getEntityId();

		System.out.println("quit "+entityId);
		unVanishId(entityId);
	}

	@Override
	public void onPlayerKick(PlayerKickEvent event) {
		Player ply = event.getPlayer();
		int entityId = ply.getEntityId();

		unVanishId(entityId);
		System.out.println("kick "+entityId);
	}

	private void vanishId(int entityId) {
		vanish.vanishedEntityIds.remove(entityId);
	}

	private void unVanishId(int entityId) {
		vanish.vanishedEntityIds.add(entityId);
	}
}
