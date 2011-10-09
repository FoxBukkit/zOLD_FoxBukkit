package de.doridian.yiffbukkit.chat;

import java.util.HashMap;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.StateContainer;
import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.YiffBukkitCommandException;

public class ChatHelper extends StateContainer {
	public HashMap<String, ChatChannel> channels = new HashMap<String, ChatChannel>();
	
	public void joinChannel(Player player, ChatChannel channel) {
		String plyname = player.getName().toLowerCase();
		if(!channel.players.containsKey(plyname)) {
			channel.players.put(plyname, true);
		}
	}
	
	public void leaveChannel(Player player, ChatChannel channel) {
		String plyname = player.getName().toLowerCase();
		if(channel.players.containsKey(plyname)) {
			channel.players.remove(plyname);
		}
	}
	
	public ChatChannel addChannel(Player owner, String name) throws YiffBukkitCommandException {
		return addChannel(owner, name, false);
	}
	
	public ChatChannel addChannel(Player owner, String name, boolean overwrite) throws YiffBukkitCommandException {
		name = name.toLowerCase();
		ChatChannel newChan;
		if(channels.containsKey(name)) {
			if(overwrite) {
				newChan = channels.get(name);
			} else {
				throw new YiffBukkitCommandException("Channel exists already!");
			}
		} else {
			newChan = new ChatChannel(name);
			channels.put(name, newChan);
		}
		newChan.owner = owner.getName().toLowerCase();
		return newChan;
	}
	
	public ChatChannel getChannel(String name) throws YiffBukkitCommandException {
		if(channels.containsKey(name)) {
			return channels.get(name);
		} else {
			throw new YiffBukkitCommandException("Channel does not exist!");
		}
	}
	
	
	private static ChatHelper instance;
	YiffBukkit plugin;
	ChatHelper(YiffBukkit plug) {
		plugin = plug;
		instance = this;
	}
	
	public static ChatHelper getInstance() {
		return instance;
	}
}
