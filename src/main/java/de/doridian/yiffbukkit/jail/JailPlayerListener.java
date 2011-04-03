package de.doridian.yiffbukkit.jail;

import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.PluginManager;

import de.doridian.yiffbukkit.YiffBukkit;

public class JailPlayerListener extends PlayerListener {
	private final YiffBukkit plugin;
	private final JailEngine jailEngine;

	public JailPlayerListener(JailEngine jailEngine) {
		plugin = jailEngine.plugin;
		this.jailEngine = jailEngine;

		PluginManager pm = plugin.getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_JOIN, this, Priority.Highest, plugin);
		pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, this, Priority.Lowest, plugin);
		pm.registerEvent(Event.Type.PLAYER_RESPAWN, this, Priority.Highest, plugin);
	}

	@Override
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (jailEngine.isJailed(event.getPlayer()))
			jailEngine.rejailPlayer(event.getPlayer());
	}

	@Override
	public void onPlayerCommandPreprocess(PlayerChatEvent event) {
		if(jailEngine.isJailed(event.getPlayer()))
			event.setCancelled(true);
	}

	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		if (jailEngine.isJailed(event.getPlayer()))
			event.setRespawnLocation(event.getPlayer().getLocation());
	}

}
