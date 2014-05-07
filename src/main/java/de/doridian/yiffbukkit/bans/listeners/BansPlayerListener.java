package de.doridian.yiffbukkit.bans.listeners;

import de.doridian.yiffbukkit.bans.Ban;
import de.doridian.yiffbukkit.bans.BanPlayer;
import de.doridian.yiffbukkit.bans.BanResolver;
import de.doridian.yiffbukkit.bans.LockDownMode;
import de.doridian.yiffbukkit.core.YiffBukkit;
import de.doridian.yiffbukkit.core.util.MessageHelper;
import de.doridian.yiffbukkit.core.util.PermissionPredicate;
import de.doridian.yiffbukkit.main.listeners.BaseListener;
import de.doridian.yiffbukkit.main.util.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

public class BansPlayerListener extends BaseListener {
	private final boolean IS_BAN_AND_ALT_CHECK_ENABLED = Configuration.getValue("enable-ban-and-alt-check", "true").equalsIgnoreCase("true");

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		if (!IS_BAN_AND_ALT_CHECK_ENABLED || plugin.bans.lockdownMode != LockDownMode.OFF)
			return;

		String name = event.getName();
		UUID uuid = event.getUniqueId();

		Ban ban = BanResolver.getBan(name, uuid);
		if(ban == null) {
			ban = BanResolver.getBan("[IP]" + event.getAddress().getHostAddress(), null);
			if(ban != null) {
				ban.setUser(name, uuid);
				ban.refreshTime();
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

	public static String makePossibleAltString(String user, UUID uuid) {
		final Collection<BanPlayer> alts = BanResolver.getPossibleAltsForPlayer(user, uuid);
		if(alts == null || alts.isEmpty())
			return null;

		final StringBuilder sb = new StringBuilder();

		boolean notFirst = false;
		boolean hasBans = false;
		for (BanPlayer alt : alts) {
			final Ban altBan = BanResolver.getBan(alt.name, alt.uuid);

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

			sb.append(alt.name);
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
				final String user = player.getName();
				final UUID uuid = player.getUniqueId();

				if(IS_BAN_AND_ALT_CHECK_ENABLED)
					BanResolver.addIPForPlayer(user, uuid, player.getAddress().getAddress());

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
}
