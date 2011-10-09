package de.doridian.yiffbukkit.chat.manager;

import org.bukkit.entity.Player;

public class ChatLogEntry {
	private final String text;
	private final Object origin;

	public ChatLogEntry(String text, Object origin) {
		this.text = text;
		this.origin = origin;
	}

	public String getText() {
		return text;
	}

	public String toString() {
		return text;
	}

	public void send(Player ply) {
		ply.sendMessage(text);
	}

	public Object getOrigin() {
		return origin;
	}
}
