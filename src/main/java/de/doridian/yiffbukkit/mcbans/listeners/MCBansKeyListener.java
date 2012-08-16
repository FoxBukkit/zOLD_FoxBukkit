package de.doridian.yiffbukkit.mcbans.listeners;

import de.doridian.yiffbukkit.main.listeners.BaseListener;
import de.doridian.yiffbukkit.mcbans.MCBansGUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.getspout.spoutapi.event.input.KeyReleasedEvent;
import org.getspout.spoutapi.keyboard.Keyboard;

public class MCBansKeyListener extends BaseListener {
	@EventHandler(priority = EventPriority.MONITOR)
	public void onKeyReleasedEvent(KeyReleasedEvent event) {
		if(event.getKey().equals(Keyboard.KEY_F9)) {
			new MCBansGUI(plugin, event.getPlayer());
		}
	}
}
