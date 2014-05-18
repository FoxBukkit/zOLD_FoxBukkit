package de.doridian.yiffbukkit.chat;

import de.doridian.dependencies.redis.AbstractRedisHandler;
import de.doridian.dependencies.redis.RedisManager;
import de.doridian.yiffbukkit.core.YiffBukkit;
import de.doridian.yiffbukkit.main.chat.Parser;
import org.bukkit.entity.Player;

import java.util.UUID;

public class RedisHandler extends AbstractRedisHandler {
	public RedisHandler() {
		super("yiffbukkit:to_server_xml");
	}

	public static void sendMessage(final Player player, final String  message) {
		if(player == null || message == null)
			throw new NullPointerException();
		RedisManager.publish("yiffbukkit:from_server", YiffBukkit.instance.configuration.getValue("server-name", "Main") + "|" + player.getUniqueId() + "|" + player.getName() + "|" + message);
	}

	@Override
	public void onMessage(final String c_message) {
		try {
			final String[] split = c_message.split("\0", 5);

			// SERVER\0 UUID\0 NAME\0 format\0 param1\0 param2
			final String server = split[0];
			@SuppressWarnings("UnusedDeclaration")
			final UUID userUUID = UUID.fromString(split[1]);
			@SuppressWarnings("UnusedDeclaration")
			final String userName = split[2];
			String format = split[3];
			final Object[] params = split[4].split("\0");

			if (!server.equals(YiffBukkit.instance.configuration.getValue("server-name", "Main"))) {
				format = "<color name=\"dark_green\">[" + server + "]</color> " + format;
			}

			Parser.sendToAll(format, params);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
