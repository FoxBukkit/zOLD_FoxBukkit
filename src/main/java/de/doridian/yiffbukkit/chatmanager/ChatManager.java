package de.doridian.yiffbukkit.chatmanager;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;

import net.minecraft.server.PacketListener;
import net.minecraft.server.Packet;
import net.minecraft.server.Packet3Chat;

import org.bukkit.entity.Player;
import de.doridian.yiffbukkit.YiffBukkit;

public class ChatManager {
	YiffBukkit plugin;
	Stack<Object> currentOrigin = new Stack<Object>();

	Map<String, Queue<ChatEntry>> chatQueues = new HashMap<String, Queue<ChatEntry>>();

	private Queue<ChatEntry> getChatQueue(Player ply) {
		Queue<ChatEntry> chatQueue = chatQueues.get(ply.getName());
		if (chatQueue == null)
			return addPlayerEntry(ply);

		return chatQueue;
	}

	private Queue<ChatEntry> addPlayerEntry(Player ply) {
		Queue<ChatEntry> chatQueue = new ArrayBlockingQueue<ChatEntry>(40);

		chatQueues.put(ply.getName(), chatQueue);
		return chatQueue;
	}

	public ChatManager(YiffBukkit plugin) {
		this.plugin = plugin;

		final PacketListener packetListener = new PacketListener() {
			@Override
			public boolean onOutgoingPacket(Player ply, int packetID, Packet packet) {
				String text = ((Packet3Chat)packet).a;

				ChatEntry chatEntry = new ChatEntry(text, getCurrentOrigin());

				Queue<ChatEntry> chatQueue = getChatQueue(ply);
				chatQueue.add(chatEntry);
				if (chatQueue.size() > 20)
					chatQueue.poll();

				return true;
			}
		};
		PacketListener.addPacketListener(true, 3, packetListener);
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

		for (ChatEntry chatEntry : chatQueue) {
			ply.sendRawMessage(chatEntry.getText());
		}
	}
}
