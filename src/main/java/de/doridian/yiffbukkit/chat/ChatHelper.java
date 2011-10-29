package de.doridian.yiffbukkit.chat;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.dynmap.Client;

import de.doridian.yiffbukkit.StateContainer;
import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.YiffBukkitCommandException;

public class ChatHelper extends StateContainer {
	public final ChatChannelContainer container;
	public final ChatChannel DEFAULT;
	public final ChatChannel OOC;
	
	public void joinChannel(Player player, ChatChannel channel) throws YiffBukkitCommandException {
		String plyname = player.getName().toLowerCase();
		if(!channel.players.containsKey(plyname)) {
			channel.players.put(plyname, true);
		} else {
			throw new YiffBukkitCommandException("Player already in channel!");
		}
		saveChannels();
	}
	
	public void leaveChannel(Player player, ChatChannel channel) throws YiffBukkitCommandException {
		if(channel == DEFAULT) throw new YiffBukkitCommandException("You cannot leave the default channel! Mute it!");
		if(channel == OOC) throw new YiffBukkitCommandException("You cannot leave the OOC channel! Mute it!");
		
		String plyname = player.getName().toLowerCase();
		if(channel.players.containsKey(plyname)) {
			if(container.activeChannel.get(plyname) == channel) {
				container.activeChannel.put(plyname, DEFAULT);
			}
			channel.players.remove(plyname);
		} else {
			throw new YiffBukkitCommandException("Player not in channel!");
		}
		saveChannels();
	}
	
	public ChatChannel addChannel(Player owner, String name) throws YiffBukkitCommandException {
		return addChannel(owner, name, false);
	}
	
	public ChatChannel addChannel(Player owner, String name, boolean overwrite) throws YiffBukkitCommandException {
		String keyname = name.toLowerCase();
		ChatChannel newChan;
		if(container.channels.containsKey(keyname)) {
			if(overwrite) {
				newChan = container.channels.get(keyname);
			} else {
				throw new YiffBukkitCommandException("Channel exists already!");
			}
		} else {
			newChan = new ChatChannel(name);
			container.channels.put(keyname, newChan);
		}
		
		try {
			newChan.owner = owner.getName().toLowerCase();
		} catch(Exception e) { newChan.owner = null; }
		
		saveChannels();
		return newChan;
	}
	
	@SuppressWarnings("unchecked")
	public void removeChannel(ChatChannel channel) {
		for(Entry<String,ChatChannel> entry : ((HashMap<String,ChatChannel>)container.activeChannel.clone()).entrySet()) {
			if(entry.getValue() == channel) {
				container.activeChannel.put(entry.getKey(), DEFAULT);
			}
		}
		
		channel.players.clear();
		channel.moderators.clear();
		channel.users.clear();
		channel.owner = null;
		channel.mode = ChatChannel.ChatChannelMode.PRIVATE;
		
		String channame = channel.name.toLowerCase();
		
		container.channels.remove(channame);
		
		saveChannels();
	}
	
	public ChatChannel getChannel(String name) throws YiffBukkitCommandException {
		name = name.toLowerCase();
		if(container.channels.containsKey(name)) {
			return container.channels.get(name);
		} else {
			throw new YiffBukkitCommandException("Channel does not exist!");
		}
	}
	
	public ChatChannel getActiveChannel(Player ply) {
		if(ply == null) return DEFAULT;
		
		ChatChannel chan = container.activeChannel.get(ply.getName().toLowerCase());
		if(chan == null) {
			verifyPlayerInDefaultChannel(ply);
			return DEFAULT;
		}
		
		return chan;
	}
	
	public void verifyPlayerInDefaultChannel(Player ply) {
		if(ply == null) return;
		
		boolean needsSave = false;
		
		String plyname = ply.getName().toLowerCase();
		
		if(container.activeChannel.get(plyname) == null) {
			container.activeChannel.put(plyname, DEFAULT);
			needsSave = true;
		}
		
		try {
			joinChannel(ply, DEFAULT);
			needsSave = true;
		} catch(Exception e) { }
		
		try {
			joinChannel(ply, OOC);
			needsSave = true;
		} catch(Exception e) { }
		
		if(needsSave) saveChannels();
		
		plugin.playerHelper.sendDirectedMessage(ply, "WARNING: Default channel is now *LOCAL*");
		plugin.playerHelper.sendDirectedMessage(ply, "To talk to everyone, you do /ooc MESSAGE");
	}
	
	public void sendChat(Player ply, String msg, boolean format) throws YiffBukkitCommandException {
		sendChat(ply, msg, format, null);
	}
	
	public void sendChat(Player ply, String msg, boolean format, ChatChannel chan) throws YiffBukkitCommandException {
		if(chan == null) chan = getActiveChannel(ply);
		if(!chan.canSpeak(ply)) {
			throw new YiffBukkitCommandException("You cannot speak in this channel!");
		}
		
		if(format && ply != null) {
			if(chan == OOC) {
				plugin.ircbot.sendToPublicChannel("[" + ply.getName() + "]: " + msg);
				try {
					if (plugin.dynmap != null) {
						plugin.dynmap.mapManager.pushUpdate(new Client.ChatMessage("player", "", ply.getDisplayName(), msg, ply.getName()));
					}
				}
				catch(Exception e) { }
			}
			msg = plugin.playerHelper.getPlayerTag(ply) + ply.getDisplayName() + ":§f " + msg;
		}
		
		int canhear = 0;
		
		if(chan != DEFAULT) msg = "§2[" + chan.name + "]§f " + msg;
		
		for(Entry<String,Boolean> entry : chan.players.entrySet()) {
			if(!entry.getValue()) continue; //for speed!
			
			Player player = plugin.getServer().getPlayerExact(entry.getKey());
			if(player == null) continue;
			
			if(chan.canHear(player, ply)) {
				player.sendRawMessage(msg);
				if(player != ply) canhear++;
			}
		}
		
		if(chan.range > 0 && canhear < 1) {
			plugin.playerHelper.sendDirectedMessage(ply, "No one can hear you (forgot /ooc?)");
		}
	}
	
	private static ChatHelper instance;
	YiffBukkit plugin;
	ChatHelper(YiffBukkit plug) {
		plugin = plug;
		instance = this;
		
		ChatChannelContainer cont = null;
		try {
			FileInputStream stream = new FileInputStream("channels.dat");
			ObjectInputStream reader = new ObjectInputStream(stream);
			try {
				cont = (ChatChannelContainer)reader.readObject();
			} catch(Exception e) {
				e.printStackTrace();
				cont = null;
			}
			reader.close();
			stream.close();
		} catch(Exception e) { }
		
		if(cont == null) container = new ChatChannelContainer();
		else container = cont;
		
		ChatChannel cc = null;
		try {
			cc = addChannel(null, "DEFAULT", true);
		} catch(Exception e) { }
		DEFAULT = cc;
		
		cc = null;
		try {
			cc = addChannel(null, "OOC", true);
		} catch(Exception e) { }
		OOC = cc;
		
		saveChannels();
	}
	
	public static ChatHelper getInstance() {
		return instance;
	}
	
	@Saver({"channels"})
	public static void saveChannels() {
		try {
			FileOutputStream stream = new FileOutputStream("channels.dat");
			ObjectOutputStream writer = new ObjectOutputStream(stream);
			try {
				writer.writeObject(ChatHelper.instance.container);
			} catch(Exception e) {
				e.printStackTrace();
			}		
			writer.close();
			stream.close();
		} catch(Exception e) { }
	}
}
