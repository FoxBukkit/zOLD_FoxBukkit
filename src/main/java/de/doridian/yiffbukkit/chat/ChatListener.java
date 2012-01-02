package de.doridian.yiffbukkit.chat;

import de.doridian.yiffbukkit.YiffBukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

public class ChatListener extends PlayerListener {
	ChatHelper helper;
	YiffBukkit plugin;
	//ChatScreenListener screen;

	public ChatListener(YiffBukkit plug) {
		plugin = plug;
		helper = new ChatHelper(plugin);
		//screen = new ChatScreenListener(plugin);
		plugin.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_CHAT, this, Priority.Highest, plugin);
		plugin.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, this, Priority.Monitor, plugin);
		plugin.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_QUIT, this, Priority.Monitor, plugin);
	}

	@Override
	public void onPlayerChat(PlayerChatEvent event) {
		if (event.isCancelled()) return;

		String msg = event.getMessage();
		char fchar = msg.charAt(0);
		if (fchar == '/' || fchar == '#')
			return;

		try {
			helper.sendChat(event.getPlayer(), msg, true);
		}
		catch (Exception e) {
			plugin.playerHelper.sendDirectedMessage(event.getPlayer(), e.getMessage());
		}

		event.setCancelled(true);
	}

	@Override
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player ply = event.getPlayer();
		helper.verifyPlayerInDefaultChannel(ply);
		//screen.getPopupFor((SpoutPlayer)ply);
	}

	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
		//screen.removePopupFor((SpoutPlayer)event.getPlayer());
	}
}
