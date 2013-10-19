package de.doridian.yiffbukkit.chat;

import de.doridian.yiffbukkit.main.util.Configuration;
import de.doridian.yiffbukkit.main.util.RedisManager;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class RedisHandler extends JedisPubSub implements Runnable {
	@Override
	public void run() {
		RedisManager.readJedisPool.getResource().subscribe(this, "yiffbukkit:to_server");
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
			//SERVER|USER|MESSAGE
			final String[] split = c_message.split("\\|", 3);
			final String server = split[0];
			final Player user = YiffBukkit.instance.playerHelper.matchPlayerSingle(split[1], true);
			String message = split[2];
			if(!server.equals(Configuration.getValue("server-name", "Main"))) {
				message = "\u00a72[" + server + "]\u00a7f " + message;
				Bukkit.broadcastMessage(message);
				return;
			}
			ChatHelper chatHelper = ChatHelper.getInstance();
			chatHelper.sendChat(user, message, false, chatHelper.DEFAULT);
		} catch (Exception e) {
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
