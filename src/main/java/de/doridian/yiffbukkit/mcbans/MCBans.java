package de.doridian.yiffbukkit.mcbans;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.offlinebukkit.OfflinePlayer;

public class MCBans {
	private YiffBukkit plugin;
	@SuppressWarnings("unused")
	private MCBansPlayerListener listener;
	
	private MCBansBlockLogger logger;

	public MCBans(YiffBukkit plug) {
		plugin = plug;
		listener = new MCBansPlayerListener(plug);
		logger = new MCBansBlockLoggerLogBlock(plug);
	}


	public enum BanType {
		GLOBAL, LOCAL, TEMPORARY;
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
						plugin.playerHelper.sendDirectedMessage(from, "Player with the name " + ply + " was not banned!");
						break;
					case 's':
						plugin.playerHelper.sendDirectedMessage(from, "Player " + ply + " is banned from another server in a group this server is part of!");
						break;
					case 'e':
					default:
						plugin.playerHelper.sendDirectedMessage(from, "Error while unbanning player " + ply + "!");
						break;
				}
			}
		}.start();
	}

	public void ban(final CommandSender from, final Player ply, final String reason, final BanType type, final boolean saveproof) {
		if (type == BanType.TEMPORARY) return;
		ban(from, ply, reason, type, 0, "", saveproof);
	}

	public void ban(final CommandSender from, final Player ply, final String reason, final BanType type, final long duration, final String measure, final boolean saveproof) {
		String addr;
		if (ply instanceof OfflinePlayer) addr = "";
		else addr = ply.getAddress().getAddress().getHostAddress();
		ban(from, ply.getName(), ply.getWorld(), addr, reason, type, duration, measure, saveproof);
	}

	public void ban(final CommandSender from, final String ply, final World world, final String ip, final String reason, final BanType type, final boolean saveproof) {
		if (type == BanType.TEMPORARY) return;
		ban(from, ply, world, ip, reason, type, 0, "", saveproof);
	}

	public void ban(final CommandSender from, final String ply, final World world, final String ip, final String reasonx, final BanType type, final long duration, final String measure, final boolean saveproof) {
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
				String reason = reasonx;

				if (saveproof) {
					if (world != null) {
						long proofid = evidence(from, ply, world);
						if (proofid > 0) reason += " proofid#" + proofid;
					}

					if (from instanceof Player) {
						World otherworld = ((Player)from).getWorld();
						if (otherworld != null && otherworld != world) {
							long proofid = evidence(from, ply, otherworld);
							if (proofid > 0) reason += " proofid#" + proofid;
						}
					}
				}

				JSONObject banret = MCBansUtil.apiQuery("exec="+exec+"&admin="+MCBansUtil.URLEncode(from.getName())+"&playerip="+MCBansUtil.URLEncode(ip)+"&reason="+MCBansUtil.URLEncode(reason)+"&player="+MCBansUtil.URLEncode(ply)+"&duration="+duration+"&measure="+MCBansUtil.URLEncode(measure));
				char result = ((String)banret.get("result")).charAt(0);
				switch (result) {
					case 'a':
						plugin.playerHelper.sendDirectedMessage(from, "Player with the name " + ply + " was already banned!");
						break;
					case 's':
						plugin.playerHelper.sendDirectedMessage(from, "Player " + ply + " is banned from another server in our servergroup(s)!");
						break;
					case 'y':
						plugin.playerHelper.sendServerMessage(from.getName() + " banned " + ply + " [Reason: " + reason + "]!");
						break;
					case 'w':
						plugin.playerHelper.sendDirectedMessage(from, "Could not ban " + ply + " because ban contained badword: " + (String)banret.get("word"));
						break;
					default:
					case 'e':
						plugin.playerHelper.sendDirectedMessage(from, "Error while banning player " + ply + "!");
						break;
				}
			}
		}.start();
	}

	public long evidence(final CommandSender from, final String ply, World world) {
		if (world == null) return 0;
		String tmp = logger.getFormattedBlockChangesBy(ply, world, false, false);
		JSONObject ret = MCBansUtil.apiQuery("exec=evidence&admin="+MCBansUtil.URLEncode(from.getName())+"&player="+MCBansUtil.URLEncode(ply)+"&changes="+MCBansUtil.URLEncode(tmp));
		long proofID = (Long)ret.get("value");
		if (proofID == 31) return 0; //i dunno why, but meh...
		tmp = "Saved evidence for " + ply + " in world " + world.getName() + " as ID: " + proofID;
		System.out.println(tmp);
		plugin.playerHelper.sendDirectedMessage(from, tmp);
		return proofID;
	}
}
