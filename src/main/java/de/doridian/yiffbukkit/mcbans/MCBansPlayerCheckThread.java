package de.doridian.yiffbukkit.mcbans;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

public class MCBansPlayerCheckThread extends Thread {
	private Player ply;
	private final MCBansPlayerListener listener;
	public MCBansPlayerCheckThread(MCBansPlayerListener listenerx, Player plyx) {
		ply = plyx;
		listener = listenerx;
	}

	@Override
	public void run() {
		if(ply == null)
			return;

		final String name = ply.getName();
		final JSONObject connret = MCBansUtil.apiQuery("player="+MCBansUtil.URLEncode(name)+"&exec=user_connect&version=YiffBukkit");

		final Player lookupPlayer = listener.plugin.getServer().getPlayer(name);
		if(lookupPlayer == null) {
			listener.doneAuthing(ply);
			return;
		}

		if (!lookupPlayer.isOnline()) {
			listener.doneAuthing(lookupPlayer);
			return;
		}

		if(connret == null) {
			listener.doneAuthing(lookupPlayer);
			kickPlayer("[YB] Sorry, mcbans.com API failure, please rejoin!");
			return;
		}
		//{"ban_status":"b","ban_num":1,"owner":"n","disputes":0,"reputation":"10.00","new_version":"y","ban_local_reason":null,"is_mcbans_mod":"n"}
		//b = bans on record, g = global, l = local, t = temporary, n = no bans on record
		char utype = ((String)connret.get("ban_status")).charAt(0);
		switch(utype) {
		case 'g':
			setPlayerRank(name, "banned"); //just making sure :3
			kickPlayer("[YB] You are globally banned! See mcbans.com");
			break;

		case 'l':
			setPlayerRank(name, "banned");
			kickPlayer("[YB] You are banned from this server!");
			break;

		case 't':
			kickPlayer("[YB] Temporary ban. Rejoin in "+ ((String)connret.get("ban_remain")));
			break;

		case 'b':
			listener.plugin.playerHelper.sendServerMessage(name + " has " + connret.get("ban_num") +  " ban(s) on record! ("+connret.get("reputation")+" REP)", 3);
			/* FALL-THROUGH */

		default:
			long disputes = (Long)connret.get("disputes");
			if(disputes > 0) {
				sendDirectedMessage("You have "+disputes+" open dispute(s)!");
			}

			if (connret.get("is_mcbans_mod").equals("y")) listener.plugin.playerHelper.sendServerMessage(name + " is an MCBans moderator!");
			break;
		}
		listener.doneAuthing(lookupPlayer);
	}

	private void setPlayerRank(final String name, final String rank) {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(listener.plugin, new Runnable() {
			public void run() {
				listener.plugin.playerHelper.setPlayerRank(name, rank);
			}
		});
	}

	private void sendDirectedMessage(final String msg) {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(listener.plugin, new Runnable() {
			public void run() {
				listener.plugin.playerHelper.sendDirectedMessage(ply, msg);
			}
		});
	}

	private void kickPlayer(final String message) {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(listener.plugin, new Runnable() {
			public void run() {
				ply.kickPlayer(message);
			}
		});
	}
}
