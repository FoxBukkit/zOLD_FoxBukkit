package de.doridian.yiffbukkit.mcbans;

import de.doridian.yiffbukkit.YiffBukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.event.input.KeyReleasedEvent;
import org.getspout.spoutapi.keyboard.Keyboard;

public class MCBansKeyListener implements Listener {
	final YiffBukkit plugin;

	public MCBansKeyListener(YiffBukkit plug) {
		plugin = plug;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onKeyReleasedEvent(KeyReleasedEvent event) {
		if(event.getKey().equals(Keyboard.KEY_F9)) {
			new MCBansGUI(plugin, event.getPlayer());
		}
	}
}
