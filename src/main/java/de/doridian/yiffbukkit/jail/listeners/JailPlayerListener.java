package de.doridian.yiffbukkit.jail.listeners;

import de.doridian.yiffbukkit.jail.JailEngine;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class JailPlayerListener implements Listener {
	private final JailEngine jailEngine;

	public JailPlayerListener(JailEngine jailEngine) {
		this.jailEngine = jailEngine;

		Bukkit.getPluginManager().registerEvents(this, jailEngine.plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (jailEngine.isJailed(event.getPlayer()))
			jailEngine.rejailPlayer(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if(jailEngine.isJailed(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		if (jailEngine.isJailed(event.getPlayer()))
			event.setRespawnLocation(event.getPlayer().getLocation());
	}
}
