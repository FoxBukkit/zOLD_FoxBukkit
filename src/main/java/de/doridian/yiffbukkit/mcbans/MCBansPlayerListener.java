package de.doridian.yiffbukkit.mcbans;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.PluginManager;

import de.doridian.yiffbukkit.YiffBukkit;

public class MCBansPlayerListener extends PlayerListener {
	protected Set<String> authingPlayers = Collections.synchronizedSet(new HashSet<String>());
	protected YiffBukkit plugin;

	public MCBansPlayerListener(YiffBukkit plug) {
		plugin = plug;
		PluginManager pm = plugin.getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_LOGIN, this, Event.Priority.High, plugin);
	}

	protected void doneAuthing(Player ply) {
		synchronized(authingPlayers) {
			authingPlayers.remove(ply.getName().toLowerCase());
		}
	}

	@Override
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player ply = event.getPlayer();
		synchronized(authingPlayers) {
			authingPlayers.add(ply.getName().toLowerCase());
		}
		(new MCBansPlayerCheckThread(this, ply)).start();
	}
}
