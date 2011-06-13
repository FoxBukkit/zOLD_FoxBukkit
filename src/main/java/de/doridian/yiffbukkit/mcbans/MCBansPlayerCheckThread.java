package de.doridian.yiffbukkit.mcbans;

import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

public class MCBansPlayerCheckThread extends Thread {
	private Player ply;
	private MCBansPlayerListener listener;
	public MCBansPlayerCheckThread(MCBansPlayerListener listenerx, Player plyx) {
		ply = plyx;
		listener = listenerx;
	}
	
	@Override
	public void run() {
		if(ply == null) return;
		String name = ply.getName();
		JSONObject connret = MCBansUtil.apiQuery("player="+MCBansUtil.URLEncode(name)+"&exec=user_connect&version=YiffBukkit");
		ply = listener.plugin.getServer().getPlayer(name);
		if(ply == null || !ply.isOnline()) {
			listener.authingPlayers.remove(name.toLowerCase());
			return;
		}
		if(connret == null) {
			listener.authingPlayers.remove(name.toLowerCase());
			ply.kickPlayer("[YB] Sorry, mcbans.com API failure, please rejoin!");
			return;
		}
		//{"ban_status":"b","ban_num":1,"owner":"n","disputes":0,"reputation":"10.00","new_version":"y","ban_local_reason":null,"is_mcbans_mod":"n"}
		//b = bans on record, g = global, l = local, t = temporary, n = no bans on record
		char utype = ((String)connret.get("ban_status")).charAt(0);
		switch(utype) {
			case 'g':
				listener.plugin.playerHelper.setPlayerRank(name, "banned"); //just making sure :3
				ply.kickPlayer("[YB] You are globally banned! See mcbans.com");
				break;
			case 'l':
				listener.plugin.playerHelper.setPlayerRank(name, "banned"); //just making sure :3
				ply.kickPlayer("[YB] You are banned from this server!");
				break;
			case 't':
				ply.kickPlayer("[YB] Temporary ban. Rejoin in "+ ((String)connret.get("ban_remain")));
				break;
			default:
				long disputes = (Long)connret.get("disputes");
				if(disputes > 0) listener.plugin.playerHelper.sendDirectedMessage(ply, "You have "+disputes+" open dispute(s)!");
				
				if(((String)connret.get("is_mcbans_mod")).equalsIgnoreCase("y")) listener.plugin.playerHelper.sendServerMessage(ply.getName() + " is an MCBans moderator!");
				
				if(utype == 'b') listener.plugin.playerHelper.sendServerMessage(name + " has " + connret.get("ban_num").toString() +  " ban(s) on record! ("+((String)connret.get("reputation"))+" REP)", 3);
				break;
		}
		listener.doneAuthing(ply);
	}
}
