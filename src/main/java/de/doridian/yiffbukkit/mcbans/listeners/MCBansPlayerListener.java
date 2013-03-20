package de.doridian.yiffbukkit.mcbans.listeners;

import de.doridian.yiffbukkit.main.listeners.BaseListener;
import de.doridian.yiffbukkit.mcbans.Ban;
import de.doridian.yiffbukkit.mcbans.BanResolver;
import de.doridian.yiffbukkitsplit.LockDownMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

public class MCBansPlayerListener extends BaseListener {
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		if (plugin.lockdownMode != LockDownMode.OFF)
			return;

		String name = event.getName();

		Ban ban = BanResolver.getBan(name);
		if(ban != null) {
			event.disallow(Result.KICK_BANNED, "[YB] Banned: " + ban.getReason());
		}
	}

	/*
	private void sendIRCMessage(final String msg) {
		org.bukkit.Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				plugin.ircbot.sendToStaffChannel(msg);
			}
		});
	}

	private void sendServerMessage(final String msg, final String permission) {
		org.bukkit.Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				plugin.playerHelper.sendServerMessage(msg, permission);
			}
		});
	}
	*/
}
