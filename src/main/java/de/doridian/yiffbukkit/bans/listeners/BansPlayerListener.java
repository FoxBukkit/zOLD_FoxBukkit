package de.doridian.yiffbukkit.bans.listeners;

import de.doridian.yiffbukkit.main.listeners.BaseListener;
import de.doridian.yiffbukkit.bans.Ban;
import de.doridian.yiffbukkit.bans.BanResolver;
import de.doridian.yiffbukkitsplit.LockDownMode;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import de.doridian.yiffbukkitsplit.util.MessageHelper;
import de.doridian.yiffbukkitsplit.util.PermissionPredicate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;

import java.net.InetAddress;
import java.util.Collection;

public class BansPlayerListener extends BaseListener {
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
				final Collection<String> alts = BanResolver.getPossibleAltsForPlayer(user);
				if(alts == null || alts.isEmpty())
					return;

				final StringBuilder sb = new StringBuilder();

				boolean notFirst = false;
				boolean hasBans = false;
				for (String alt : alts) {
					final Ban altBan = BanResolver.getBan(alt);

					if (notFirst)
						sb.append(", ");
					else
						notFirst = true;

					if (altBan != null) {
						hasBans = true;
						sb.append("\u00a7c");
					}
					else
						sb.append("\u00a7a");

					sb.append(alt);
				}

				final String message;
				if (hasBans)
					message = String.format("%1$s has some banned possible alts: %2$s", user, sb);
				else
					message = String.format("Possible alts of %1$s: %2$s", user, sb);

				Bukkit.getScheduler().scheduleSyncDelayedTask(YiffBukkit.instance, new Runnable() {
					@Override
					public void run() {
						MessageHelper.sendColoredServerMessage("light_purple", new PermissionPredicate("yiffbukkit.opchat"), message);
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
