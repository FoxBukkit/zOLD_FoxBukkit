package de.doridian.yiffbukkit.chat.manager;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
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
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.Packet;
import org.bukkit.event.server.PacketListener;

import de.doridian.yiffbukkit.YiffBukkit;

public class ChatManager {
	private static final ChatLogEntry EMPTY_CHAT_LOG_ENTRY = new ChatLogEntry("", null);
	private static final int CHAT_QUEUE_LENGTH = 100;

	private static final int SPAM_WINDOW = 20;
	private static final int SPAM_COUNT = 5;
	private static final int SPAM_TIME_WINDOW = 10000; // ms

	private static final int RATE_TIME_WINDOW = 500; // ms
	private static final int RATE_LIMIT = 4;

	YiffBukkit plugin;
	Stack<Object> currentOrigin = new Stack<Object>();

	Map<String, Queue<ChatLogEntry>> chatQueues = new HashMap<String, Queue<ChatLogEntry>>();

	private Queue<ChatLogEntry> getChatQueue(Player ply) {
		return chatQueues.get(ply.getName());
	}

	Map<Player, LinkedList<ChatEntry>> lastPlayerMessages = new HashMap<Player, LinkedList<ChatEntry>>();
	public ChatManager(YiffBukkit plugin) {
		this.plugin = plugin;

		final PacketListener packetListener = new PacketListener() {
			@Override
			public boolean onOutgoingPacket(Player ply, int packetID, Packet packet) {
				String text = ((Packet3Chat)packet).message;

				ChatLogEntry chatEntry = new ChatLogEntry(text, getCurrentOrigin());

				Queue<ChatLogEntry> chatQueue = getChatQueue(ply);
				if (chatQueue == null)
					return false;

				chatQueue.add(chatEntry);
				if (chatQueue.size() > CHAT_QUEUE_LENGTH)
					chatQueue.poll();

				return true;
			}


			@Override
			public boolean onIncomingPacket(Player ply, int packetID, Packet packet) {
				LinkedList<ChatEntry> spamWindow = lastPlayerMessages.get(ply);
				if (spamWindow == null)
					return false;

				String text = ((Packet3Chat)packet).message;

				if (text.charAt(0) == '/')
					return true;

				if (!spamWindow.isEmpty()) {
					long minTime = System.currentTimeMillis() - SPAM_TIME_WINDOW;
					while (!spamWindow.isEmpty() && spamWindow.peek().getTime() < minTime) {
						spamWindow.remove();
					}

					if (spamWindow.size() >=  RATE_LIMIT && System.currentTimeMillis() - spamWindow.get(spamWindow.size() - RATE_LIMIT).getTime() < RATE_TIME_WINDOW) {
						ply.kickPlayer("flood");

						filterPlayer(ply);

						resendAll();

						return false;
					}

					int count = 0;
					for (ChatEntry prev : spamWindow) {
						if (prev.getText().equals(text))
							++count;

						if (count >= SPAM_COUNT)
							break;
					}

					if (count >= SPAM_COUNT) {
						ply.kickPlayer("spam");

						filterPlayer(ply);

						resendAll();

						return false;
					}
				}

				// add to queue
				spamWindow.add(new ChatEntry(text, System.currentTimeMillis()));

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
				final ArrayBlockingQueue<ChatLogEntry> chatQueue = new ArrayBlockingQueue<ChatLogEntry>(CHAT_QUEUE_LENGTH+1);
				for (int i = 0; i < CHAT_QUEUE_LENGTH; ++i) {
					chatQueue.offer(EMPTY_CHAT_LOG_ENTRY);
				}
				chatQueues.put(event.getPlayer().getName(), chatQueue);
				lastPlayerMessages.put(event.getPlayer(), new LinkedList<ChatEntry>());
			}

			@Override
			public void onPlayerKick(PlayerKickEvent event) {
				lastPlayerMessages.remove(event.getPlayer());
				chatQueues.remove(event.getPlayer().getName());
			}

			@Override
			public void onPlayerQuit(PlayerQuitEvent event) {
				lastPlayerMessages.remove(event.getPlayer());
				chatQueues.remove(event.getPlayer().getName());
			}
		};

		plugin.getServer().getPluginManager().registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Lowest, plugin);
		plugin.getServer().getPluginManager().registerEvent(Type.PLAYER_KICK, playerListener, Priority.Lowest, plugin);
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
		Queue<ChatLogEntry> chatQueue = chatQueues.get(ply.getName());
		if (chatQueue == null)
			return;

		for (ChatLogEntry chatEntry : new ArrayBlockingQueue<ChatLogEntry>(CHAT_QUEUE_LENGTH+1, false, chatQueue)) {
			ply.sendRawMessage(chatEntry.getText());
		}

	}

	public void filterChats(String regex) {
		for (Player ply : plugin.getServer().getOnlinePlayers()) {
			filterChat(regex, ply);
		}
	}

	public void filterChat(String regex, Player ply) {
		Queue<ChatLogEntry> chatQueue = chatQueues.get(ply.getName());

		if (chatQueue == null)
			return;

		int removed = 0;

		for (Iterator<ChatLogEntry> it = chatQueue.iterator(); it.hasNext(); ) {
			ChatLogEntry chatEntry = it.next();
			if (chatEntry.getText().matches(regex)) {
				it.remove();
				++removed;
			}
		}

		if (removed > 0) {
			Queue<ChatLogEntry> newChatQueue = new ArrayBlockingQueue<ChatLogEntry>(CHAT_QUEUE_LENGTH+1);

			for (int i = 0; i < removed; ++i)
				newChatQueue.offer(EMPTY_CHAT_LOG_ENTRY);

			newChatQueue.addAll(chatQueue);
			chatQueues.put(ply.getName(), chatQueue = newChatQueue);

			resend(ply);
		}
	}

	private void filterPlayer(Player ply) {
		for (Entry<String, Queue<ChatLogEntry>> bar : chatQueues.entrySet()) {
			for (Iterator<ChatLogEntry> it = bar.getValue().iterator(); it.hasNext();) {
				ChatLogEntry chatEntry = it.next();
				if (ply.equals(chatEntry.getOrigin()))
					it.remove();
			}
		}
	}
}
