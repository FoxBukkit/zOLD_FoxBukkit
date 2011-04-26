package de.doridian.yiffbukkit.chatmanager;

import org.bukkit.entity.Player;

public class ChatEntry {
	private String text;
	private Object origin;

	public ChatEntry(String text, Object origin) {
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
