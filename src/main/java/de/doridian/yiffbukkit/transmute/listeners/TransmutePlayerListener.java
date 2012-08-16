package de.doridian.yiffbukkit.transmute.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.doridian.yiffbukkit.main.listeners.BaseListener;
import de.doridian.yiffbukkit.transmute.Shape;
import de.doridian.yiffbukkit.transmute.Transmute;

public class TransmutePlayerListener extends BaseListener {
	final Transmute transmute;

	public TransmutePlayerListener(Transmute transmute) {
		this.transmute = transmute;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Shape shape = transmute.getShape(event.getPlayer());

		if (shape == null)
			return;

		System.out.println("Rejoined with shape - this shouldn't happen.");
		//shape.rejoin();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerKick(PlayerKickEvent event) {
		if (event.isCancelled())
			return;

		transmute.removeShape(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		transmute.removeShape(event.getPlayer());
	}
}
