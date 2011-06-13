package de.doridian.yiffbukkit.mcbans;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.offlinebukkit.OfflinePlayer;

public class MCBans {
	private YiffBukkit plugin;
	private MCBansPlayerListener listener;
	
	public MCBans(YiffBukkit plug) {
		plugin = plug;
		listener = new MCBansPlayerListener(plug);
	}
	
	public boolean isAuthing(Player ply) {
		return listener.authingPlayers.contains(ply.getName().toLowerCase());
	}
	
	
	public enum BanType {
		GLOBAL, LOCAL, TEMPORARY;
	}

	public enum BanTimeMeasure {
		MINUTES, HOURS, DAYS;
		public static String valueOf(BanTimeMeasure measure) {
			if(measure == null) return "";
			switch(measure) {
				case MINUTES:
					return "m";
				case HOURS:
					return "h";
				case DAYS:
					return "d";
				default:
					return "";
			}
		}
	}

	
	public void unban(final CommandSender from, final String ply) {
		new Thread() {
			public void run() {
				JSONObject unbanret = MCBansUtil.apiQuery("exec=unban_user&player=" + MCBansUtil.URLEncode(ply));
				if(((String)unbanret.get("result")).equalsIgnoreCase("n")) plugin.playerHelper.sendDirectedMessage(from, "Player with the name " + ply + " was not banned!");
				else plugin.playerHelper.sendServerMessage(from.getDisplayName() + " unbanned " + ply + "!");
			}
		}.start();
	}

	public void ban(final CommandSender from, final Player ply, final String reason, final BanType type) {
		if(type == BanType.TEMPORARY) return;
		ban(from, ply, reason, type, 0, null);
	}
	
	public void ban(final CommandSender from, final Player ply, final String reason, final BanType type, final long duration, final BanTimeMeasure measure) {
		String addr;
		if(ply instanceof OfflinePlayer) addr = "";
		else addr = ply.getAddress().toString();
		ban(from, ply.getName(), addr, reason, type, duration, measure);
	}
	
	public void ban(final CommandSender from, final String ply, final String ip, final String reason, final BanType type) {
		if(type == BanType.TEMPORARY) return;
		ban(from, ply, ip, reason, type, 0, null);
	}
	
	public void ban(final CommandSender from, final String ply, final String ip, final String reason, final BanType type, final long duration, final BanTimeMeasure measure) {
		new Thread() {
			public void run() {
				String exec;
				if(type == null) return;
				switch(type) {
					case GLOBAL:
						exec = "ban_user";
						break;
					case LOCAL:
						exec = "ban_local_user";
						break;
					case TEMPORARY:
						exec = "tempban_user";
						break;
					default:
						return;
				}
				JSONObject banret = MCBansUtil.apiQuery("exec="+exec+"&admin="+from.getName()+"&playerip="+MCBansUtil.URLEncode(ip)+"&reason="+MCBansUtil.URLEncode(reason)+"&player="+MCBansUtil.URLEncode(ply)+"&duration="+duration+"&measure="+BanTimeMeasure.valueOf(measure));
				char result = ((String)banret.get("result")).charAt(0);
				switch(result) {
					case 'a':
						plugin.playerHelper.sendDirectedMessage(from, "Player with the name " + ply + " was already banned!");
						break;
					case 'n':
						plugin.playerHelper.sendDirectedMessage(from, "Player with the name " + ply + " could not be banned!");
						break;
					default:
						plugin.playerHelper.sendServerMessage(from.getDisplayName() + " banned " + ply + "!");
						break;
				}
			}
		}.start();
	}
}
