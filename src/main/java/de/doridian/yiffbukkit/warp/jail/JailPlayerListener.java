package de.doridian.yiffbukkit.warp.jail;

import de.doridian.yiffbukkitsplit.YiffBukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class JailPlayerListener implements Listener {
	private final YiffBukkit plugin;
	private final JailEngine jailEngine;

	public JailPlayerListener(JailEngine jailEngine) {
		plugin = jailEngine.plugin;
		this.jailEngine = jailEngine;

		plugin.getServer().getPluginManager().registerEvents(this, plugin);
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
