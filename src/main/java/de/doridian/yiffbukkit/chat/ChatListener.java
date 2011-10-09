package de.doridian.yiffbukkit.chat;

import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

import de.doridian.yiffbukkit.YiffBukkit;

public class ChatListener extends PlayerListener {
	ChatHelper helper;
	
	YiffBukkit plugin;
	public ChatListener(YiffBukkit plug) {
		plugin = plug;
		helper = new ChatHelper(plugin);
		plug.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_CHAT, this, Priority.Highest, plugin);
	}
	
	@Override
	public void onPlayerChat(PlayerChatEvent event) {
		
	}
}
