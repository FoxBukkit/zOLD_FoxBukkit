package de.doridian.yiffbukkit.mcbans;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.json.simple.JSONObject;

import de.doridian.yiffbukkit.YiffBukkit;

public class MCBansPlayerListener extends PlayerListener {
	protected YiffBukkit plugin;

	public MCBansPlayerListener(YiffBukkit plug) {
		plugin = plug;
		PluginManager pm = plugin.getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_PRELOGIN, this, Event.Priority.High, plugin);
		pm.registerEvent(Event.Type.PLAYER_QUIT, this, Event.Priority.High, plugin);
	}
	
	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (plugin.serverClosed)
			return;
		
		MCBansUtil.apiQuery("exec=playerDisconnect&player="+MCBansUtil.URLEncode(event.getPlayer().getName()));
	}

	@Override
	public void onPlayerPreLogin(PlayerPreLoginEvent event) {
		if (plugin.serverClosed)
			return;

		String name = event.getName();
		final JSONObject connret = MCBansUtil.apiQuery("exec=playerConnect&player="+MCBansUtil.URLEncode(name)+"&playerip="+MCBansUtil.URLEncode(event.getAddress().getHostAddress()));

		if(connret == null) {
			event.disallow(Result.KICK_OTHER, "[YB] Sorry, mcbans.com API failure, please rejoin!");
			return;
		}
		//{"banStatus":"l","banReason":"so shut up :3","playerRep":10,"altList":"kraventoxic: REP 0, kravent0xic: REP 10"}
		//b = bans on record, g = global, l = local, t = temporary, n = no bans on record, s = servergroup, i = invalid ip
		char utype = ((String)connret.get("banStatus")).toLowerCase().charAt(0);
		switch(utype) {
		case 'g':
			event.disallow(Result.KICK_BANNED, "[YB] You are globally banned! See mcbans.com");
			break;

		case 'l':
			event.disallow(Result.KICK_BANNED, "[YB] Locally Banned: " + ((String)connret.get("banReason")));
			break;

		case 't':
			event.disallow(Result.KICK_BANNED, "[YB] Temporary ban: " + ((String)connret.get("banReason")));
			break;
			
		case 's':
			event.disallow(Result.KICK_BANNED, "[YB] You are banned from another server in one of this server's servergroups!");
			break;

		case 'b':
			sendIRCMessage(name + "has previous bans and "+connret.get("playerRep")+" REP");
			sendServerMessage(name + "has previous bans and "+connret.get("playerRep")+" REP", 3);
			/* FALL-THROUGH */

		case 'n':
			if(connret.containsKey("altList")) {
				sendIRCMessage(name + " has potential alts: "+connret.get("altList"));
				sendServerMessage(name + " has potential alts: "+connret.get("altList"), 3);
			}
			break;
			
		case 'i':
			event.disallow(Result.KICK_BANNED, "[YB] Invalid IP for that account!");
			break;
			
		default:
			event.disallow(Result.KICK_BANNED, "[YB] You have some kind of ban I don't know what it is, ask mcbans.com");
			break;
		}
	}
	
	private void sendIRCMessage(final String msg) {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				plugin.ircbot.sendToStaffChannel(msg);
			}
		});
	}

	private void sendServerMessage(final String msg, final int color) {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				plugin.playerHelper.sendServerMessage(msg, color);
			}
		});
	}
}
