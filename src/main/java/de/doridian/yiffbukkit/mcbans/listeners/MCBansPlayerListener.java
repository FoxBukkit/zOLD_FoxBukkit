package de.doridian.yiffbukkit.mcbans.listeners;

import de.doridian.yiffbukkit.main.listeners.BaseListener;
import de.doridian.yiffbukkit.mcbans.MCBansUtil;
import de.doridian.yiffbukkitsplit.LockDownMode;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent.Result;
import org.json.simple.JSONObject;

public class MCBansPlayerListener extends BaseListener {
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		if (plugin.lockdownMode != LockDownMode.OFF)
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
			String rep = connret.get("playerRep").toString();
			sendIRCMessage(name + " has previous bans and "+rep+" REP");
			if(plugin.playerHelper.getPlayerLevel(name) < 10)
				sendServerMessage(name + " has previous bans and "+rep+" REP", "mcbans.joinstats");
			else
				sendServerMessage(name + " has previous bans and "+rep+" REP", "mcbans.joinstats.owner");
			/* FALL-THROUGH */

		case 'n':
			if(connret.containsKey("altList")) {
				String alts = ((String)connret.get("altList")).trim();
				if(alts.length() > 0) {
					sendIRCMessage(name + " has potential alts: "+alts);
					if(plugin.playerHelper.getPlayerLevel(name) < 10)
						sendServerMessage(name + " has potential alts: "+alts, "mcbans.joinstats");
					else
						sendServerMessage(name + " has potential alts: "+alts, "mcbans.joinstats.owner");
				}
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

	private void sendServerMessage(final String msg, final String permission) {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				plugin.playerHelper.sendServerMessage(msg, permission);
			}
		});
	}
}
