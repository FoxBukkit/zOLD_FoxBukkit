package de.doridian.yiffbukkit.chat;

import de.doridian.yiffbukkit.main.chat.Parser;
import de.doridian.yiffbukkit.main.util.Configuration;
import de.doridian.yiffbukkit.main.util.RedisManager;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class RedisHandler extends JedisPubSub implements Runnable {
	@Override
	public void run() {
		RedisManager.readJedisPool.getResource().subscribe(this, "yiffbukkit:to_server_xml");
	}

	public static void sendMessage(final Player player, final String  message) {
		if(player == null || message == null)
			throw new NullPointerException();
		final Jedis jedis = RedisManager.readJedisPool.getResource();
		jedis.publish("yiffbukkit:from_server", Configuration.getValue("server-name", "Main") + "|" + player.getName() + "|" + message);
		RedisManager.readJedisPool.returnResource(jedis);
	}

	public static void initialize() {
		new Thread(new RedisHandler()).start();
	}

	@Override
	public void onMessage(final String channel, final String c_message) {
		try {
			final String[] split = c_message.split("\0", 4);

			// SERVER\0 USER\0 format\0 param1\0 param2
			final String server = split[0];
			@SuppressWarnings("UnusedDeclaration")
			final String userName = split[1];
			String format = split[2];
			final Object[] params = split[3].split("\0");

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
