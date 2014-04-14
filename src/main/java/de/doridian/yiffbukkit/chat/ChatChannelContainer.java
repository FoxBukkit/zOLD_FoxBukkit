package de.doridian.yiffbukkit.chat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ChatChannelContainer implements Serializable {
	private static final long serialVersionUID = 2L;

	/**
	 * Maps player name to active chat channel.
	 */
	public HashMap<UUID, ChatChannel> activeChannel = new HashMap<>();

	/**
	 * Maps channel name to chat channel.
	 */
	public HashMap<String, ChatChannel> channels = new HashMap<>();

	public List<ChatReplacer> replacers = new ArrayList<>();
}
