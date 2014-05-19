package de.doridian.yiffbukkit.chat;

import com.google.gson.Gson;
import de.doridian.dependencies.redis.AbstractRedisHandler;
import de.doridian.dependencies.redis.RedisManager;
import de.doridian.yiffbukkit.chat.json.ChatMessage;
import de.doridian.yiffbukkit.core.YiffBukkit;
import de.doridian.yiffbukkit.core.util.PlayerHelper;
import de.doridian.yiffbukkit.main.chat.Parser;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class RedisHandler extends AbstractRedisHandler {
	public RedisHandler() {
		super("yiffbukkit:to_server");
	}

	public static void sendMessage(final Player player, final String  message) {
		if(player == null || message == null)
			throw new NullPointerException();
		RedisManager.publish("yiffbukkit:from_server", YiffBukkit.instance.configuration.getValue("server-name", "Main") + "|" + player.getUniqueId() + "|" + player.getName() + "|" + message);
	}

	private final Gson gson = new Gson();

	@Override
	public void onMessage(final String c_message) {
		try {
			final ChatMessage chatMessage;
			synchronized (gson) {
				chatMessage = gson.fromJson(c_message, ChatMessage.class);
			}

			if (!chatMessage.server.equals(YiffBukkit.instance.configuration.getValue("server-name", "Main"))) {
				chatMessage.contents.plain = "\u00a72[" + chatMessage.server + "]\u00a7f " + chatMessage.contents.plain;
				if(chatMessage.contents.xml_format != null)
					chatMessage.contents.xml_format = "<color name=\"dark_green\">[" + chatMessage.server + "]</color> " + chatMessage.contents.xml_format;
			}

			List<Player> allPlayers = Arrays.asList(YiffBukkit.instance.getServer().getOnlinePlayers());
			List<Player> targetPlayers = new ArrayList<>();
			switch(chatMessage.to.type) {
				case "all":
					targetPlayers = allPlayers;
					break;
				case "permission":
					for(String permission : chatMessage.to.filter)
						for (Player player : allPlayers)
							if (player.hasPermission(permission) && !targetPlayers.contains(player))
								targetPlayers.add(player);
					break;
				case "player":
					for(String playerUUID : chatMessage.to.filter)
						for (Player player : allPlayers)
							if (player.getUniqueId().equals(UUID.fromString(playerUUID)) && !targetPlayers.contains(player))
								targetPlayers.add(player);
					break;
			}

			if(chatMessage.contents.xml_format != null)
				Parser.sendToPlayers(targetPlayers, chatMessage.contents.xml_format, chatMessage.contents.xml_format_args);
			else
				for(Player plyTarget : targetPlayers)
					plyTarget.sendMessage(chatMessage.contents.plain);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
