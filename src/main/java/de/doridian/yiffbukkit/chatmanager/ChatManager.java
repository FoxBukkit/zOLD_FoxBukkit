package de.doridian.yiffbukkit.chatmanager;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;

import net.minecraft.server.Packet3Chat;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.Packet;
import org.bukkit.event.server.PacketListener;

import de.doridian.yiffbukkit.YiffBukkit;

public class ChatManager {
	private static final int CHAT_QUEUE_LENGTH = 100;
	private static final int SPAM_WINDOW = 20;
	YiffBukkit plugin;
	Stack<Object> currentOrigin = new Stack<Object>();

	Map<String, Queue<ChatEntry>> chatQueues = new HashMap<String, Queue<ChatEntry>>();

	private Queue<ChatEntry> getChatQueue(Player ply) {
		return chatQueues.get(ply.getName());
	}

	Map<Player, Queue<String>> lastPlayerMessages = new HashMap<Player, Queue<String>>();
	public ChatManager(YiffBukkit plugin) {
		this.plugin = plugin;

		final PacketListener packetListener = new PacketListener() {
			@Override
			public boolean onOutgoingPacket(Player ply, int packetID, Packet packet) {
				String text = ((Packet3Chat)packet).a;

				ChatEntry chatEntry = new ChatEntry(text, getCurrentOrigin());

				Queue<ChatEntry> chatQueue = getChatQueue(ply);
				if (chatQueue == null)
					return false;

				chatQueue.add(chatEntry);
				if (chatQueue.size() > CHAT_QUEUE_LENGTH)
					chatQueue.poll();

				return true;
			}


			@Override
			public boolean onIncomingPacket(Player ply, int packetID, Packet packet) {
				Queue<String> spamWindow = lastPlayerMessages.get(ply);
				if (spamWindow == null)
					return false;

				String text = ((Packet3Chat)packet).a;

				if (text.charAt(0) == '/')
					return true;

				int count = 0;
				for (String prev : spamWindow) {
					if (prev.equals(text))
						++count;

					if (count >= 5)
						break;
				}

				if (count >= 5) {
					ply.kickPlayer("spam");

					for (Entry<String, Queue<ChatEntry>> bar : chatQueues.entrySet()) {
						for (Iterator<ChatEntry> it = bar.getValue().iterator(); it.hasNext();) {
							ChatEntry chatEntry = it.next();
							if (ply.equals(chatEntry.getOrigin()))
								it.remove();
						}
					}

					resendAll();

					return false;
				}

				// add to queue
				spamWindow.add(text);

				// limit queue size
				if (spamWindow.size() > SPAM_WINDOW)
					spamWindow.remove();

				return true;
			}
		};
		PacketListener.addPacketListener(true, 3, packetListener, plugin);
		PacketListener.addPacketListener(false, 3, packetListener, plugin);

		PlayerListener playerListener = new PlayerListener() {
			@Override
			public void onPlayerJoin(PlayerJoinEvent event) {
				chatQueues.put(event.getPlayer().getName(), new ArrayBlockingQueue<ChatEntry>(CHAT_QUEUE_LENGTH+1));
				lastPlayerMessages.put(event.getPlayer(), new ArrayBlockingQueue<String>(SPAM_WINDOW+1));
			}
			@Override
			public void onPlayerQuit(PlayerQuitEvent event) {
				lastPlayerMessages.remove(event.getPlayer());
				chatQueues.remove(event.getPlayer().getName());
			}
		};

		plugin.getServer().getPluginManager().registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Lowest, plugin);
		plugin.getServer().getPluginManager().registerEvent(Type.PLAYER_QUIT, playerListener, Priority.Lowest, plugin);
	}

	public Object getCurrentOrigin() {
		try {
			return currentOrigin.peek();
		}
		catch (EmptyStackException e) {
			return null;
		}
	}

	public void pushCurrentOrigin(Object currentOrigin) {
		this.currentOrigin.push(currentOrigin);
	}

	public void popCurrentOrigin() {
		this.currentOrigin.pop();
	}

	public void resendAll() {
		for (Player ply : plugin.getServer().getOnlinePlayers()) {
			resend(ply);
		}
	}

	private void resend(Player ply) {
		Queue<ChatEntry> chatQueue = chatQueues.get(ply.getName());
		if (chatQueue == null)
			return;

		for (ChatEntry chatEntry : new ArrayBlockingQueue<ChatEntry>(CHAT_QUEUE_LENGTH+1, false, chatQueue)) {
			ply.sendRawMessage(chatEntry.getText());
		}

	}

	public void filterChat(String regex) {
		for (Entry<String, Queue<ChatEntry>> bar : chatQueues.entrySet()) {
			for (Iterator<ChatEntry> it = bar.getValue().iterator(); it.hasNext();) {
				ChatEntry chatEntry = it.next();
				if (chatEntry.getText().matches(regex))
					it.remove();
			}
		}

		resendAll();
	}
}
