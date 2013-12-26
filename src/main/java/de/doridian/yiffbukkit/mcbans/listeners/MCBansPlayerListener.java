package de.doridian.yiffbukkit.mcbans.listeners;

import de.doridian.yiffbukkit.main.listeners.BaseListener;
import de.doridian.yiffbukkit.mcbans.Ban;
import de.doridian.yiffbukkit.mcbans.BanResolver;
import de.doridian.yiffbukkitsplit.LockDownMode;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;

public class MCBansPlayerListener extends BaseListener {
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		if (plugin.lockdownMode != LockDownMode.OFF)
			return;

		String name = event.getName();

		Ban ban = BanResolver.getBan(name);
		if(ban != null)
			event.disallow(Result.KICK_BANNED, "[YB] Banned: " + ban.getReason());
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();

		new Thread() {
			public void run() {
				final InetAddress playerIP = player.getAddress().getAddress();

				final String user = player.getName();

				BanResolver.addIPForPlayer(user, playerIP);
				Collection<String> alts = BanResolver.getPossibleAltsForPlayer(user);
				if(alts == null || alts.isEmpty())
					return;

				StringBuilder message = new StringBuilder();

				message.append("Possible alts of ");
				message.append(user);
				message.append(": ");

				boolean notFirst = false;
				for(String alt : alts) {
					Ban altBan = BanResolver.getBan(alt);

					if(notFirst)
						message.append(", ");
					else
						notFirst = true;

					if(altBan != null)
						message.append("\u00a7c");
					else
						message.append("\u00a7a");

					message.append(alt);
				}

				final String msgStr = message.toString();

				Bukkit.getScheduler().scheduleSyncDelayedTask(YiffBukkit.instance, new Runnable() {
					@Override
					public void run() {
						PlayerHelper.broadcastMessage("\u00a7d[YB]\u00a7f " + msgStr, "yiffbukkit.opchat");
					}
				});
			}
		}.start();
	}

	/*
	private void sendServerMessage(final String msg, final String permission) {
		org.bukkit.Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				plugin.playerHelper.sendServerMessage(msg, permission);
			}
		});
	}
	*/
}
