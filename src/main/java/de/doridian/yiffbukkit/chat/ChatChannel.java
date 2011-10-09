package de.doridian.yiffbukkit.chat;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.entity.Player;

public class ChatChannel {
	public ChatChannelMode mode = ChatChannelMode.PUBLIC;
	public final String name;
	public String password = "";
	
	public HashSet<String> users = new HashSet<String>();
	public HashSet<String> moderators = new HashSet<String>();
	public String owner;
	
	public int range = 0;
	
	public HashMap<String,Boolean> players = new HashMap<String,Boolean>();
	
	public ChatChannel(String name) {
		this.name = name;
	}
	
	public boolean canJoin(Player ply) {
		return canJoin(ply, "");
	}
	
	public boolean canJoin(Player ply, String pass) {
		String plyname = ply.getName().toLowerCase();
		
		if(users.contains(plyname) || moderators.contains(plyname)) return true;
		
		if(mode != ChatChannelMode.PRIVATE && (password.isEmpty() || pass.equals(password))) return true;
		
		return false;
	}
	
	public boolean canHear(Player target, Player source) {
		if(target == source) return true;
		
		String tname = target.getName().toLowerCase();
		
		//is the player in the channel?
		if(!players.containsKey(tname)) {
			return false;
		}
		
		//is the player listening to the channel?
		if(!players.get(tname)) {
			return false;
		}
		
		//is the player in range of the channel?
		if(range > 0 && (target.getWorld() != source.getWorld() || target.getLocation().distance(source.getLocation()) > range)) {
			return false;
		}
		
		return true;
	}
	
	public boolean canSpeak(Player player) {
		String pname = player.getName().toLowerCase();
		
		//is the player in the channel?
		if(!players.containsKey(pname)) {
			return false;
		}
		
		//if channel is moderated, is user in the users list?
		if(mode == ChatChannelMode.MODERATED && !users.contains(pname)) {
			return false;
		}
		
		return true;
	}
	
	public enum ChatChannelMode {
		PUBLIC, PRIVATE, MODERATED;
	}
}
