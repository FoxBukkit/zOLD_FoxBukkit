package de.doridian.yiffbukkit.mcbans;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent.Result;
import org.bukkit.plugin.PluginManager;
import org.json.simple.JSONObject;

import de.doridian.yiffbukkit.YiffBukkit;

public class MCBansPlayerListener extends PlayerListener {
	protected YiffBukkit plugin;

	private HashMap<String,Integer> disputeCount = new HashMap<String,Integer>();

	public MCBansPlayerListener(YiffBukkit plug) {
		plugin = plug;
		PluginManager pm = plugin.getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_PRELOGIN, this, Event.Priority.High, plugin);
		pm.registerEvent(Event.Type.PLAYER_JOIN, this, Event.Priority.High, plugin);
	}

	@Override
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player ply = event.getPlayer();
		String name = ply.getName().toLowerCase();
		Integer disputes = disputeCount.get(name);
		if(disputes == null) return;
		disputeCount.remove(name);
		int disps = (int)disputes;
		if(disps <= 0) return;
		plugin.playerHelper.sendDirectedMessage(ply, "You have "+disps+" open dispute(s)!");
	}

	@Override
	public void onPlayerPreLogin(PlayerPreLoginEvent event) {
		if (plugin.serverClosed) return;
		String name = event.getName();
		final JSONObject connret = MCBansUtil.apiQuery("player="+MCBansUtil.URLEncode(name)+"&exec=user_connect&version=YiffBukkit");

		if(connret == null) {
			event.disallow(Result.KICK_OTHER, "[YB] Sorry, mcbans.com API failure, please rejoin!");
			return;
		}
		//{"ban_status":"b","ban_num":1,"owner":"n","disputes":0,"reputation":"10.00","new_version":"y","ban_local_reason":null,"is_mcbans_mod":"n"}
		//b = bans on record, g = global, l = local, t = temporary, n = no bans on record
		char utype = ((String)connret.get("ban_status")).charAt(0);
		switch(utype) {
		case 'g':
			event.disallow(Result.KICK_BANNED, "[YB] You are globally banned! See mcbans.com");
			break;

		case 'l':
			event.disallow(Result.KICK_BANNED, "[YB] You are banned from this server!");
			break;

		case 't':
			event.disallow(Result.KICK_BANNED, "[YB] Temporary ban. Rejoin in "+ ((String)connret.get("ban_remain")));
			break;

		case 'b':
			sendServerMessage(name + " has " + connret.get("ban_num") +  " ban(s) on record! ("+connret.get("reputation")+" REP)", 3);
			/* FALL-THROUGH */

		default:
			long disputes = (Long)connret.get("disputes");
			if(disputes > 0) {
				disputeCount.put(name.toLowerCase(), (Integer)(int)disputes);
			} else {
				disputeCount.remove(name.toLowerCase());
			}

			if (connret.get("is_mcbans_mod").equals("y")) sendServerMessage(name + " is an MCBans moderator!");
			break;
		}
	}

	private void sendServerMessage(final String msg, final int color) {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				plugin.playerHelper.sendServerMessage(msg, color);
			}
		});
	}

	private void sendServerMessage(final String msg) {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				plugin.playerHelper.sendServerMessage(msg);
			}
		});
	}
}
