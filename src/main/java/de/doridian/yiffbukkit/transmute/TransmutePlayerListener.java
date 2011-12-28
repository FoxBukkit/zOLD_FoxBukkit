package de.doridian.yiffbukkit.transmute;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;

public class TransmutePlayerListener extends PlayerListener {
	final Transmute transmute;

	public TransmutePlayerListener(Transmute transmute) {
		this.transmute = transmute;

		final PluginManager pm = transmute.plugin.getServer().getPluginManager();
		pm.registerEvent(Type.PLAYER_JOIN, this, Priority.Monitor, transmute.plugin);
		pm.registerEvent(Type.PLAYER_KICK, this, Priority.Monitor, transmute.plugin);
		pm.registerEvent(Type.PLAYER_QUIT, this, Priority.Monitor, transmute.plugin);
	}

	@Override
	public void onPlayerJoin(PlayerJoinEvent event) {
		Shape shape = transmute.getShape(event.getPlayer());

		if (shape == null)
			return;

		System.out.println("Rejoined with shape - this shouldn't happen.");
		//shape.rejoin();
	}

	@Override
	public void onPlayerKick(PlayerKickEvent event) {
		if (event.isCancelled())
			return;

		transmute.removeShape(event.getPlayer());
	}

	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
		transmute.removeShape(event.getPlayer());
	}
}
