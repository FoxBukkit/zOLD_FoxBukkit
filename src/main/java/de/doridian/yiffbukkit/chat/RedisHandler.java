package de.doridian.yiffbukkit.chat;

import de.doridian.yiffbukkit.main.chat.Parser;
import de.doridian.yiffbukkit.main.util.Configuration;
import de.doridian.yiffbukkit.main.util.RedisManager;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.UUID;

public class RedisHandler extends JedisPubSub implements Runnable {
	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(1000);
				RedisManager.readJedisPool.getResource().subscribe(this, "yiffbukkit:to_server_xml");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void sendMessage(final Player player, final String  message) {
		if(player == null || message == null)
			throw new NullPointerException();
		final Jedis jedis = RedisManager.readJedisPool.getResource();
		jedis.publish("yiffbukkit:from_server", Configuration.getValue("server-name", "Main") + "|" + player.getUniqueId() + "|" + player.getName() + "|" + message);
		RedisManager.readJedisPool.returnResource(jedis);
	}

	public static void initialize() {
		Thread t = new Thread(new RedisHandler());
		t.setDaemon(true);
		t.start();
	}

	@Override
	public void onMessage(final String channel, final String c_message) {
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

			if (!server.equals(Configuration.getValue("server-name", "Main"))) {
				format = "<color name=\"dark_green\">[" + server + "]</color> " + format;
			}

			Parser.sendToAll(format, params);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPMessage(String s, String s2, String s3) {

	}

	@Override
	public void onSubscribe(String s, int i) {

	}

	@Override
	public void onUnsubscribe(String s, int i) {

	}

	@Override
	public void onPUnsubscribe(String s, int i) {

	}

	@Override
	public void onPSubscribe(String s, int i) {

	}
}
