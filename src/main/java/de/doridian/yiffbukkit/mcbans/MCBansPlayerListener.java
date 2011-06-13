package de.doridian.yiffbukkit.mcbans;

import java.util.Vector;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.PluginManager;

import de.doridian.yiffbukkit.YiffBukkit;

public class MCBansPlayerListener extends PlayerListener {
	protected Vector<String> authingPlayers = new Vector<String>();
	protected YiffBukkit plugin;
	
	public MCBansPlayerListener(YiffBukkit plug) {
		plugin = plug;
		PluginManager pm = plugin.getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_LOGIN, this, Event.Priority.High, plugin);
	}
	
	protected void doneAuthing(Player ply) {
		authingPlayers.remove(ply.getName().toLowerCase());
	}
	
	@Override
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player ply = event.getPlayer();
		String n = ply.getName().toLowerCase();
		if(!authingPlayers.contains(n)) authingPlayers.add(n);
		(new MCBansPlayerCheckThread(this, ply)).start();
	}
}
