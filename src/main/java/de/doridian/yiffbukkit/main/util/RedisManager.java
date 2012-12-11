package de.doridian.yiffbukkit.main.util;

import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RedisManager {
	private static Jedis jedis = new JedisMultiWriter("37.59.53.199", "46.37.189.177");

	private static final String REDIS_PASSWORD = "SECRET";
	private static final int REDIS_DB = 3;

	private static class JedisMultiWriter extends Jedis {
		private final Jedis[] writerConns;

		private JedisMultiWriter(String host, String... otherHosts) {
			super(host);
			auth(REDIS_PASSWORD);
			select(REDIS_DB);
			writerConns = new Jedis[otherHosts.length];
			for(int i = 0; i < otherHosts.length; i++) {
				Jedis newConn = new Jedis(otherHosts[i]);
				newConn.auth(REDIS_PASSWORD);
				newConn.select(REDIS_DB);
				writerConns[i] = newConn;
			}
		}

		@Override
		public Long hdel(String key, String... fields) {
			for(Jedis jedis : writerConns) {
				jedis.hdel(key, fields);
			}
			return super.hdel(key, fields);
		}

		@Override
		public Long hset(String key, String field, String value) {
			for(Jedis jedis : writerConns) {
				jedis.hset(key, field, value);
			}
			return super.hset(key, field, value);
		}

		@Override
		public Long del(String... keys) {
			for(Jedis jedis : writerConns) {
				jedis.del(keys);
			}
			return super.del(keys);
		}
	}

	public static class RedisMap implements Map<String, String> {
		private final String name;
		public RedisMap(String name) {
			this.name = name;
		}

		@Override
		public int size() {
			if(!jedis.exists(name))
				return 0;
			return (int)(long)jedis.hlen(name);
		}

		@Override
		public boolean isEmpty() {
			return (size() <= 0);
		}

		@Override
		public boolean containsKey(Object key) {
			return jedis.hexists(name, key.toString());
		}

		@Override
		public boolean containsValue(Object value) {
			return values().contains(value.toString());
		}

		@Override
		public String get(Object key) {
			return jedis.hget(name, key.toString());
		}

		@Override
		public String put(String key, String value) {
			String old = get(key);
			jedis.hset(name, key, value);
			return old;
		}

		@Override
		public String remove(Object key) {
			String old = get(key);
			jedis.hdel(name, key.toString());
			return old;
		}

		@Override
		public void putAll(Map<? extends String, ? extends String> m) {
			for(Entry<? extends String, ? extends String> e : m.entrySet()) {
				jedis.hset(name, e.getKey(),  e.getValue());
			}
		}

		@Override
		public void clear() {
			jedis.del(name);
		}

		@Override
		public Set<String> keySet() {
			Set<String> keys = jedis.hkeys(name);
			if(keys == null)
				return new HashSet<String>();
			return keys;
		}

		@Override
		public Collection<String> values() {
			Collection<String> values = jedis.hvals(name);
			if(values == null)
				return new ArrayList<String>();
			return values;
		}

		@Override
		public Set<Entry<String, String>> entrySet() {
			Map<String, String> entryMap = jedis.hgetAll(name);
			if(entryMap == null)
				return new HashSet<Entry<String, String>>();
			return entryMap.entrySet();
		}
	}

	public static Map<String,String> createKeptMap(String name) {
		return new RedisMap(name);
	}
}
