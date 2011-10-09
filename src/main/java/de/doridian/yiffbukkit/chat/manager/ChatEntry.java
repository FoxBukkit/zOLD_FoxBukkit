package de.doridian.yiffbukkit.chat.manager;

public class ChatEntry {
	private final String text;
	private final long time;

	public ChatEntry(String text, long time) {
		this.text = text;
		this.time = time;
	}

	public String getText() {
		return text;
	}

	public String toString() {
		return text;
	}

	public long getTime() {
		return time;
	}
}
