package de.doridian.yiffbukkit.chat;

import de.doridian.yiffbukkit.core.YiffBukkit;
import de.doridian.yiffbukkit.core.util.PlayerHelper;
import de.doridian.yiffbukkit.main.PermissionDeniedException;
import de.doridian.yiffbukkit.main.StateContainer;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import org.bukkit.entity.Player;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map.Entry;

public class ChatHelper extends StateContainer {
	private static ChatHelper instance;

	public static ChatHelper getInstance() {
		return instance;
	}

	private final YiffBukkit plugin;
	public final ChatChannelContainer container;

	public final ChatChannel DEFAULT;

	public void joinChannel(Player player, ChatChannel channel) throws YiffBukkitCommandException {
		final String plyname = player.getName().toLowerCase();
		if (channel.players.containsKey(plyname))
			throw new YiffBukkitCommandException("Player already in channel!");

		channel.players.put(plyname, true);
		saveChannels();
	}

	public void leaveChannel(Player player, ChatChannel channel) throws YiffBukkitCommandException {
		if (channel == DEFAULT)
			throw new YiffBukkitCommandException("You cannot leave the default channel! Mute it!");

		final String plyname = player.getName().toLowerCase();
		if (!channel.players.containsKey(plyname))
			throw new YiffBukkitCommandException("Player not in channel!");

		if (container.activeChannel.get(plyname) == channel) {
			container.activeChannel.put(plyname, DEFAULT);
		}

		channel.players.remove(plyname);
		saveChannels();
	}

	public ChatChannel addChannel(Player owner, String name) throws YiffBukkitCommandException {
		return addChannel(owner, name, false);
	}

	public ChatChannel addChannel(Player owner, String name, boolean overwrite) throws YiffBukkitCommandException {
		final String key = name.toLowerCase();
		final ChatChannel newChan;
		if (container.channels.containsKey(key)) {
			if (!overwrite)
				throw new YiffBukkitCommandException("Channel exists already!");

			newChan = container.channels.get(key);
		}
		else {
			newChan = new ChatChannel(name);
			container.channels.put(key, newChan);
		}

		if (owner == null)
			newChan.owner = null;
		else
			newChan.owner = owner.getName().toLowerCase();

		saveChannels();
		return newChan;
	}

	@SuppressWarnings("unchecked")
	public void removeChannel(ChatChannel channel) {
		for (Entry<String,ChatChannel> entry : new HashMap<>(container.activeChannel).entrySet()) {
			if (entry.getValue() != channel)
				continue;

			container.activeChannel.put(entry.getKey(), DEFAULT);
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
		if (container.channels.containsKey(name)) {
			return container.channels.get(name);
		}
		else {
			throw new YiffBukkitCommandException("Channel does not exist!");
		}
	}

	public ChatChannel getActiveChannel(Player ply) {
		if (ply == null)
			return DEFAULT;

		ChatChannel chan = container.activeChannel.get(ply.getName().toLowerCase());
		if (chan == null) {
			verifyPlayerInDefaultChannel(ply);
			return DEFAULT;
		}

		return chan;
	}

	public void setActiveChannel(Player ply, ChatChannel chan) throws YiffBukkitCommandException {
		String plyname = ply.getName().toLowerCase();
		if (chan.players.containsKey(plyname)) {
			chan.players.put(plyname, true);
			container.activeChannel.put(plyname, chan);
		}
		else {
			throw new PermissionDeniedException();
		}
	}

	public void verifyPlayerInDefaultChannel(Player ply) {
		if (ply == null)
			return;

		boolean needsSave = false;

		String plyname = ply.getName().toLowerCase();

		try {
			joinChannel(ply, DEFAULT);
			needsSave = true;
		}
		catch (Exception ignored) { }

		if (container.activeChannel.get(plyname) == null) {
			container.activeChannel.put(plyname, DEFAULT);
			needsSave = true;
		}

		if (needsSave)
			saveChannels();
	}

	public void sendChat(Player ply, String msg, boolean format) throws YiffBukkitCommandException {
		sendChat(ply, msg, format, null);
	}

	public void sendChat(Player ply, String msg, boolean format, ChatChannel chan) throws YiffBukkitCommandException {
		if (chan == null) chan = getActiveChannel(ply);
		if (chan != DEFAULT && !chan.canSpeak(ply)) {
			throw new YiffBukkitCommandException("You cannot speak in this channel!");
		}

		if(ply != null && !ply.hasPermission("yiffbukkit.chatreplace.override")) {
			String tmp;
			for(ChatReplacer replacer : container.replacers) {
				tmp = replacer.replace(msg);
				if(tmp != null) msg = tmp;
			}
		}

		if (format && ply != null) {
			if (chan == DEFAULT) {
				plugin.sendConsoleMsg("<" + ply.getName() + "> " + msg, false);
				RedisHandler.sendMessage(ply, msg);
				return;
			} else {
				plugin.sendConsoleMsg("[" + chan.name + "] <" + ply.getName() + "> " + msg, false);
			}
			msg = plugin.playerHelper.formatPlayerFull(ply.getName()) + ":\u00a7f " + msg;
		}

		boolean noOneHearsYou = true;

		if (chan != DEFAULT) msg = "\u00a72[" + chan.name + "]\u00a7f " + msg;

		for (Entry<String,Boolean> entry : chan.players.entrySet()) {
			if (!entry.getValue())
				continue; //for speed!

			Player player = plugin.getServer().getPlayerExact(entry.getKey());
			if (player == null)
				continue;

			if (chan.canHear(player, ply)) {
				player.sendRawMessage(msg);
				if (ply != null && player != ply && ply.canSee(player))
					noOneHearsYou = false;
			}
		}

		if (noOneHearsYou && chan.range > 0) {
			PlayerHelper.sendDirectedMessage(ply, "No one can hear you");
		}
	}

	public ChatHelper(YiffBukkit plug) {
		plugin = plug;
		instance = this;

		ChatChannelContainer cont;
		try (
				final FileInputStream stream = new FileInputStream(YiffBukkit.instance.getDataFolder() + "/channels.dat");
				final ObjectInputStream reader = new ObjectInputStream(stream)
		) {
			cont = (ChatChannelContainer) reader.readObject();
			if (cont.replacers == null) {
				cont = new ChatChannelContainer();
			}
		} catch (Exception e) {
			e.printStackTrace();
			cont = new ChatChannelContainer();
		}

		container = cont;

		ChatChannel cc = null;
		try {
			cc = addChannel(null, "DEFAULT", true);
		}
		catch (Exception ignored) { }
		DEFAULT = cc;

		saveChannels();
	}

	@Saver("channels")
	public static void saveChannels() {
		try (
				FileOutputStream stream = new FileOutputStream(YiffBukkit.instance.getDataFolder() + "/channels.dat");
				ObjectOutputStream writer = new ObjectOutputStream(stream)
		) {
			writer.writeObject(ChatHelper.instance.container);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
