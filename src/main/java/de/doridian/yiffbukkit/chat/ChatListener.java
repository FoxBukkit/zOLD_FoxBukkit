package de.doridian.yiffbukkit.chat;

import de.doridian.yiffbukkitsplit.YiffBukkit;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ChatListener implements Listener {
	ChatHelper helper;
	YiffBukkit plugin;
	//ChatScreenListener screen;

	public ChatListener(YiffBukkit plug) {
		plugin = plug;
		helper = new ChatHelper(plugin);
		//screen = new ChatScreenListener(plugin);

		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
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
			PlayerHelper.sendDirectedMessage(event.getPlayer(), e.getMessage());
		}

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player ply = event.getPlayer();
		helper.verifyPlayerInDefaultChannel(ply);
		//screen.getPopupFor((SpoutPlayer)ply);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		//screen.removePopupFor((SpoutPlayer)event.getPlayer());
	}
}
