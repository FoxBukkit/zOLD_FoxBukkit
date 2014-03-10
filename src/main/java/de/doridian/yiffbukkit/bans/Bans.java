package de.doridian.yiffbukkit.bans;

import de.doridian.yiffbukkit.bans.listeners.BansPlayerListener;
import de.doridian.yiffbukkit.core.YiffBukkit;
import de.doridian.yiffbukkit.core.util.PlayerHelper;
import de.doridian.yiffbukkit.main.offlinebukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Bans {
	private YiffBukkit plugin;

	public LockDownMode lockdownMode = LockDownMode.OFF;

	@SuppressWarnings("unused")
	private BansPlayerListener playerListener;

	public Bans(YiffBukkit plug) {
		plugin = plug;
		playerListener = new BansPlayerListener();
	}


	public enum BanType {
		GLOBAL("global"), LOCAL("local"), TEMPORARY("temp");

		private final String name;

		BanType(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}
	}

	public void unban(final CommandSender from, final String ply) {
		if (ply.toLowerCase().matches(".*da5id_|sc4re.*"))
			return;

		new Thread() {
			public void run() {
				Ban ban = BanResolver.getBan(ply, null, false);
				if(ban != null) {
					BanResolver.deleteBan(ban);
					PlayerHelper.sendServerMessage(from.getName() + " unbanned " + ply + "!");
				} else {
					PlayerHelper.sendDirectedMessage(from, "Player with the name " + ply + " was not banned!");
				}
			}
		}.start();
	}

	public void ban(final CommandSender from, final Player ply, final String reason, final BanType type) {
		if (type == BanType.TEMPORARY) return;
		ban(from, ply, reason, type, 0, "");
	}

	public void ban(final CommandSender from, final Player ply, final String reason, final BanType type, final long duration, final String measure) {
		final String addr;
		if (ply instanceof OfflinePlayer) {
			addr = "";
		} else {
			addr = ply.getAddress().getAddress().getHostAddress();
		}
		ban(from, ply.getName(), addr, reason, type, duration, measure);
	}

	public void ban(final CommandSender from, final String ply, final String ip, final String reason, final BanType type) {
		if (type == BanType.TEMPORARY) return;
		ban(from, ply, ip, reason, type, 0, "");
	}

	public void ban(final CommandSender from, final String ply, final String ip, final String reason, final BanType type, final long duration, final String measure) {
		if (type == null) return;
		if (type == BanType.TEMPORARY) return;

		new Thread() {
			public void run() {
				Ban newBan = new Ban();
				newBan.setUser(ply, null);
				newBan.setAdmin(from.getName(), null);
				newBan.setReason(reason);
				newBan.setType(type.getName());
				BanResolver.addBan(newBan);
				PlayerHelper.sendServerMessage(from.getName() + " banned " + ply + " [Reason: " + reason + "]!");
			}
		}.start();
	}
}