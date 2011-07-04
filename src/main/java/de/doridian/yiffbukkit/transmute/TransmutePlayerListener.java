package de.doridian.yiffbukkit.transmute;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

public class TransmutePlayerListener extends PlayerListener {
	final Transmute transmute;

	public TransmutePlayerListener(Transmute transmute) {
		this.transmute = transmute;

		transmute.plugin.getServer().getPluginManager().registerEvent(Type.PLAYER_QUIT, this, Priority.Monitor, transmute.plugin);
	}

	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
		transmute.removeShape(event.getPlayer());
	}
}
