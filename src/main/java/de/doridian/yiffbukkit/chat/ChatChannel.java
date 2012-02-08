package de.doridian.yiffbukkit.chat;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkitsplit.util.PlayerNotFoundException;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

public class ChatChannel implements Serializable {
	private static final long serialVersionUID = 1L;
	
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
		if(source == null || target == source) return true;
		
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
		if(player == null) return true;
		
		String pname = player.getName().toLowerCase();
		
		//is the player in the channel?
		if(!players.containsKey(pname)) {
			return false;
		}
		
		//if channel is moderated, is user in the users list?
		if(mode == ChatChannelMode.MODERATED && !isUser(player)) {
			return false;
		}
		
		return true;
	}
	
	public void addUser(Player player) throws YiffBukkitCommandException {
		if(player == null) throw new PlayerNotFoundException();
		
		String plyname = player.getName().toLowerCase();
		if(!this.users.contains(plyname)) {
			this.users.add(plyname);
		} else {
			throw new YiffBukkitCommandException("Player is already a user of this channel!");
		}
	}
	
	public void removeUser(Player player) throws YiffBukkitCommandException {
		if(player == null) throw new PlayerNotFoundException();
		
		try {
			removeModerator(player);
		} catch(Exception e) { }
		
		String plyname = player.getName().toLowerCase();
		if(this.users.contains(plyname)) {
			this.users.remove(plyname);
		} else {
			throw new YiffBukkitCommandException("Player is not a user of this channel!");
		}
	}
	
	public void addModerator(Player player) throws YiffBukkitCommandException {
		if(player == null) throw new PlayerNotFoundException();
		
		try {
			addUser(player);
		} catch(Exception e) { }
		
		String plyname = player.getName().toLowerCase();
		if(!this.moderators.contains(plyname)) {
			this.moderators.add(plyname);
		} else {
			throw new YiffBukkitCommandException("Player is already a moderator of this channel!");
		}
	}
	
	public void removeModerator(Player player) throws YiffBukkitCommandException {
		if(player == null) throw new PlayerNotFoundException();
		
		String plyname = player.getName().toLowerCase();
		if(this.moderators.contains(plyname)) {
			this.moderators.remove(plyname);
		} else {
			throw new YiffBukkitCommandException("Player is not a moderator of this channel!");
		}
	}
	
	public boolean isOwner(Player player) {
		return player.getName().toLowerCase().equals(this.owner) || player.hasPermission("yiffbukkitsplit.channels.force.owner");
	}
	
	public boolean isModerator(Player player) {
		return isOwner(player) || moderators.contains(player.getName().toLowerCase()) || player.hasPermission("yiffbukkitsplit.channels.force.moderator");
	}
	
	public boolean isUser(Player player) {
		return isModerator(player) || users.contains(player.getName().toLowerCase());
	}
	
	public enum ChatChannelMode {
		PUBLIC, PRIVATE, MODERATED;
	}
}
