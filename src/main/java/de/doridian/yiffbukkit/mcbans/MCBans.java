package de.doridian.yiffbukkit.mcbans;

import de.doridian.yiffbukkit.main.offlinebukkit.OfflinePlayer;
import de.doridian.yiffbukkit.mcbans.listeners.MCBansPlayerListener;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MCBans {
	private YiffBukkit plugin;
	@SuppressWarnings("unused")
	private MCBansPlayerListener playerListener;

	public MCBans(YiffBukkit plug) {
		plugin = plug;
		playerListener = new MCBansPlayerListener();
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
		new Thread() {
			public void run() {
				Ban ban = BanResolver.getBan(ply, false);
				if(ban != null) {
					BanResolver.deleteBan(ban);
					plugin.playerHelper.sendServerMessage(from.getName() + " unbanned " + ply + "!");
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
				newBan.setUser(ply);
				newBan.setAdmin(from.getName());
				newBan.setReason(reason);
				newBan.setType(type.getName());
				BanResolver.addBan(newBan);
				plugin.playerHelper.sendServerMessage(from.getName() + " banned " + ply + " [Reason: " + reason + "]!");
				//PlayerHelper.sendDirectedMessage(from, "Player with the name " + ply + " was already banned!");
			}
		}.start();
	}
}
