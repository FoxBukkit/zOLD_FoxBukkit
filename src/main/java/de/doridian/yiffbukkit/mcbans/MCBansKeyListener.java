package de.doridian.yiffbukkit.mcbans;

import de.doridian.yiffbukkit.YiffBukkit;
import org.bukkit.event.Event;
import org.getspout.spoutapi.event.input.InputListener;
import org.getspout.spoutapi.event.input.KeyReleasedEvent;
import org.getspout.spoutapi.keyboard.Keyboard;

public class MCBansKeyListener extends InputListener {
	final YiffBukkit plugin;

	public MCBansKeyListener(YiffBukkit plug) {
		plugin = plug;
		plugin.getServer().getPluginManager().registerEvent(Event.Type.CUSTOM_EVENT, this, Event.Priority.Monitor, plugin);
	}

	@Override
	public void onKeyReleasedEvent(KeyReleasedEvent event) {
		if(event.getKey().equals(Keyboard.KEY_F9)) {
			new MCBansGUI(plugin, event.getPlayer());
		}
	}
}
