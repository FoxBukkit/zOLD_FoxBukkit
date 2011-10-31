package de.doridian.yiffbukkit.chat;

import java.util.HashMap;

import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.getspout.spoutapi.event.screen.ScreenCloseEvent;
import org.getspout.spoutapi.event.screen.ScreenListener;
import org.getspout.spoutapi.event.screen.ScreenOpenEvent;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.player.SpoutPlayer;

import de.doridian.yiffbukkit.YiffBukkit;

public class ChatScreenListener extends ScreenListener {
	YiffBukkit plugin;
	HashMap<String, ChatPopup> popups = new HashMap<String, ChatPopup>();
	
	public ChatScreenListener(YiffBukkit plug) {
		plugin = plug;
		plugin.getServer().getPluginManager().registerEvent(Event.Type.CUSTOM_EVENT, this, Priority.Highest, plugin);
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
	
	@Override
    public void onScreenOpen(ScreenOpenEvent event) {
		if(event.getScreenType() != ScreenType.CHAT_SCREEN) return;
		SpoutPlayer ply = event.getPlayer();
		ply.getMainScreen().attachWidget(plugin, getPopupFor(event.getPlayer()));
    }
       
    @Override
    public void onScreenClose(ScreenCloseEvent event) {
    	if(event.getScreenType() != ScreenType.CHAT_SCREEN) return;
    	SpoutPlayer ply = event.getPlayer();
    	if(ply.getMainScreen().getActivePopup() instanceof ChatPopup) {
    		ply.getMainScreen().closePopup();
    	}
    }
}
