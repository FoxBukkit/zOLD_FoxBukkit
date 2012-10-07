package de.doridian.yiffbukkit.mcbans;

import de.doridian.yiffbukkitsplit.YiffBukkit;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import de.doridian.yiffbukkit.main.offlinebukkit.OfflinePlayer;
import de.doridian.yiffbukkit.mcbans.listeners.MCBansKeyListener;
import de.doridian.yiffbukkit.mcbans.listeners.MCBansPlayerListener;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

public class MCBans {
	private YiffBukkit plugin;
	@SuppressWarnings("unused")
	private MCBansPlayerListener playerListener;
	@SuppressWarnings("unused")
	private MCBansKeyListener keyListener;
	@SuppressWarnings("unused")
	private ClientBlacklist clientBlacklist;

	public MCBans(YiffBukkit plug) {
		plugin = plug;
		playerListener = new MCBansPlayerListener();
		keyListener = new MCBansKeyListener();
		clientBlacklist = new ClientBlacklist(plug);
	}


	public enum BanType {
		GLOBAL, LOCAL, TEMPORARY
	}

	public void unban(final CommandSender from, final String ply) {
		new Thread() {
			public void run() {
				JSONObject unbanret = MCBansUtil.apiQuery("exec=unBan&admin="+MCBansUtil.URLEncode(from.getName())+"&player="+MCBansUtil.URLEncode(ply));
				char result = ((String)unbanret.get("result")).charAt(0);
				switch (result) {
					case 'y':
						plugin.playerHelper.sendServerMessage(from.getName() + " unbanned " + ply + "!");
						break;
					case 'n':
						PlayerHelper.sendDirectedMessage(from, "Player with the name " + ply + " was not banned!");
						break;
					case 's':
						PlayerHelper.sendDirectedMessage(from, "Player " + ply + " is banned from another server in a group this server is part of!");
						break;
					case 'e':
					default:
						PlayerHelper.sendDirectedMessage(from, "Error while unbanning player " + ply + "!");
						break;
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
		final String exec;
		switch (type) {
			case GLOBAL:
				exec = "globalBan";
				break;
			case LOCAL:
				exec = "localBan";
				break;
			case TEMPORARY:
				exec = "tempBan";
				break;
			default:
				return;
		}
		new Thread() {
			public void run() {
				JSONObject banret = MCBansUtil.apiQuery("exec="+exec+"&admin="+MCBansUtil.URLEncode(from.getName())+"&playerip="+MCBansUtil.URLEncode(ip)+"&reason="+MCBansUtil.URLEncode(reason)+"&player="+MCBansUtil.URLEncode(ply)+"&duration="+duration+"&measure="+MCBansUtil.URLEncode(measure));
				char result = ((String)banret.get("result")).charAt(0);
				switch (result) {
					case 'a':
						PlayerHelper.sendDirectedMessage(from, "Player with the name " + ply + " was already banned!");
						break;
					case 's':
						PlayerHelper.sendDirectedMessage(from, "Player " + ply + " is banned from another server in our servergroup(s)!");
						break;
					case 'y':
						plugin.playerHelper.sendServerMessage(from.getName() + " banned " + ply + " [Reason: " + reason + "]!");
						break;
					case 'w':
						PlayerHelper.sendDirectedMessage(from, "Could not ban " + ply + " because ban contained badword: " + (String)banret.get("word"));
						break;
					default:
					case 'e':
						PlayerHelper.sendDirectedMessage(from, "Error while banning player " + ply + "!");
						break;
				}
			}
		}.start();
	}
}
