package de.doridian.yiffbukkit.chat.listeners;

import de.doridian.yiffbukkit.chat.ChatPopup;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.event.screen.ScreenCloseEvent;
import org.getspout.spoutapi.event.screen.ScreenOpenEvent;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.player.SpoutPlayer;

import java.util.HashMap;

public class ChatScreenListener implements Listener { // not YBListener and not BaseListener, cause that'd autoregister this class
	YiffBukkit plugin;
	HashMap<String, ChatPopup> popups = new HashMap<String, ChatPopup>();

	public ChatScreenListener(YiffBukkit plug) {
		plugin = plug;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public void removePopupFor(SpoutPlayer ply) {
		String plyname = ply.getName().toLowerCase();
		try {
			ply.getMainScreen().removeWidget(popups.remove(plyname));
		} catch(Exception e) { }
	}

	public void makePopupFor(SpoutPlayer ply) {
		String plyname = ply.getName().toLowerCase();
		removePopupFor(ply);
		ChatPopup newPop = new ChatPopup(ply, plugin);
		newPop.init();
		popups.put(plyname, newPop);
	}

	public ChatPopup getPopupFor(SpoutPlayer ply) {
		String plyname = ply.getName().toLowerCase();
		if(!popups.containsKey(plyname)) {
			makePopupFor(ply);
		}
		return popups.get(plyname);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onScreenOpen(ScreenOpenEvent event) {
		if (event.getScreenType() != ScreenType.CHAT_SCREEN)
			return;

		SpoutPlayer ply = event.getPlayer();
		ply.getMainScreen().attachWidget(plugin, getPopupFor(ply));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onScreenClose(ScreenCloseEvent event) {
		if (event.getScreenType() != ScreenType.CHAT_SCREEN)
			return;

		SpoutPlayer ply = event.getPlayer();
		if(ply.getMainScreen().getActivePopup() instanceof ChatPopup) {
			ply.getMainScreen().closePopup();
		}
	}
}
