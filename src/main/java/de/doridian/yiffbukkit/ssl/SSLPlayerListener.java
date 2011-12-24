package de.doridian.yiffbukkit.ssl;

import de.doridian.yiffbukkit.YiffBukkit;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.plugin.PluginManager;

public class SSLPlayerListener extends PlayerListener {
	public SSLPlayerListener(YiffBukkit plugin) {
		PluginManager pm = plugin.getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_PRELOGIN, this, Event.Priority.Monitor, plugin);
	}

	@Override
	public void onPlayerPreLogin(PlayerPreLoginEvent event) {
		SSLUtils.setSSLState(event.getName(), false);
	}
}
