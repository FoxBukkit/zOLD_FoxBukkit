package de.doridian.yiffbukkit.ssl;

import de.doridian.yiffbukkit.YiffBukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPreLoginEvent;

public class SSLPlayerListener implements Listener {
	public SSLPlayerListener(YiffBukkit plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerPreLogin(PlayerPreLoginEvent event) {
		SSLUtils.setSSLState(event.getName(), false);
	}
}
