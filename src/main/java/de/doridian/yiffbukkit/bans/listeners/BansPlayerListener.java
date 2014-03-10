package de.doridian.yiffbukkit.bans.listeners;

import de.doridian.yiffbukkit.bans.Ban;
import de.doridian.yiffbukkit.bans.BanResolver;
import de.doridian.yiffbukkit.bans.LockDownMode;
import de.doridian.yiffbukkit.core.YiffBukkit;
import de.doridian.yiffbukkit.core.util.MessageHelper;
import de.doridian.yiffbukkit.core.util.PermissionPredicate;
import de.doridian.yiffbukkit.main.listeners.BaseListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;

public class BansPlayerListener extends BaseListener {
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		if (plugin.bans.lockdownMode != LockDownMode.OFF)
			return;

		String name = event.getName();
		String uuid = null;

		Ban ban = BanResolver.getBan(name, uuid);
		if(ban == null) {
			ban = BanResolver.getBan("[IP]" + event.getAddress().getHostAddress(), null);
			if(ban != null) {
				ban.setUser(name, uuid);
				BanResolver.addBan(ban);
			}
		}

		if(ban != null)
			event.disallow(Result.KICK_BANNED, "[YB] Banned: " + ban.getReason());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLogin(PlayerLoginEvent event) {
		final Player player = event.getPlayer();
		final String playerName = player.getName();
		if (!playerName.matches("^.*[A-Za-z].*$")) {
			event.disallow(PlayerLoginEvent.Result.KICK_BANNED, "[YB] Sorry, get some letters into your name.");
			return;
		}

		if (playerHelper.isGuest(player)) {
			switch (plugin.bans.lockdownMode) {
				case FIREWALL:
					final String ip = event.getAddress().getHostAddress();
					try {
						Runtime.getRuntime().exec("./wally I "+ip);
						System.out.println("Firewalled IP "+ip+" of player "+playerName+".");
						return;
					}
					catch (IOException e) {
						System.out.println("Failed to firewall IP "+ip+" of player "+playerName+".");
					}
				/* FALL-THROUGH */

				case KICK:
					event.disallow(PlayerLoginEvent.Result.KICK_BANNED, "[YB] Sorry, we're closed for guests right now");
				/* FALL-THROUGH */

				case OFF:
					//noinspection UnnecessaryReturnStatement
					return;
			}
		}
	}

	public static String makePossibleAltString(String user, String uuid) {
		final Collection<String> alts = BanResolver.getPossibleAltsForPlayer(user, uuid);
		if(alts == null || alts.isEmpty())
			return null;

		final StringBuilder sb = new StringBuilder();

		boolean notFirst = false;
		boolean hasBans = false;
		for (String alt : alts) {
			final Ban altBan = BanResolver.getBan(null, alt);

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

		if (hasBans)
			return String.format("%1$s has some banned possible alts: %2$s", user, sb);
		else
			return String.format("Possible alts of %1$s: %2$s", user, sb);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();

		new Thread() {
			public void run() {
				final InetAddress playerIP = player.getAddress().getAddress();

				final String user = player.getName();
				final String uuid = player.getUniqueId().toString();

				BanResolver.addIPForPlayer(user, uuid, playerIP);

				final String message = makePossibleAltString(user, uuid);
				if(message == null)
					return;

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
