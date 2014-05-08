package de.doridian.yiffbukkit.core.util;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CacheMap<K,V> implements Map<K,V> {
	private final long expiryTime;

	public CacheMap(final long expiryTime) {
		this.expiryTime = expiryTime;
		Thread cleanupThread = new Thread() {
			@Override
			public void run() {
				while(true) {
					try {
						Thread.sleep(expiryTime / 2L);
						final long currentTime = System.currentTimeMillis();
						synchronized (internalMap) {
							final Set<K> keysToRemove = new HashSet<>();
							for(Entry<K, CacheEntry<V>> entry : internalMap.entrySet())
								if(entry.getValue().expiry < currentTime)
									keysToRemove.add(entry.getKey());
							for(K key : keysToRemove)
								internalMap.remove(key);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		cleanupThread.setDaemon(true);
		cleanupThread.start();
	}

	private class CacheEntry<V> {
		private final V data;
		private final long expiry;
		private CacheEntry(V data) {
			this.data = data;
			this.expiry = System.currentTimeMillis() + expiryTime;
		}
	}
	private final HashMap<K, CacheEntry<V>> internalMap =  new HashMap<>();

	@Override
	public int size() {
		synchronized (internalMap) {
			return internalMap.size();
		}
	}

	@Override
	public boolean isEmpty() {
		synchronized (internalMap) {
			return internalMap.isEmpty();
		}
	}

	@Override
	public boolean containsKey(Object key) {
		synchronized (internalMap) {
			return internalMap.containsKey(key);
		}
	}

	@Override
	public boolean containsValue(Object value) {
		synchronized (internalMap) {
			return internalMap.containsValue(value);
		}
	}

	@Override
	public V get(Object key) {
		CacheEntry<V> cacheEntry;
		synchronized (internalMap) {
			cacheEntry = internalMap.get(key);
		}
		if(cacheEntry != null)
			return cacheEntry.data;
		return null;
	}

	@Override
	public V put(K key, V value) {
		CacheEntry<V> cacheEntry;
		synchronized (internalMap) {
			cacheEntry = internalMap.put(key, new CacheEntry<V>(value));
		}
		if(cacheEntry != null)
			return cacheEntry.data;
		return null;
	}

	@Override
	public V remove(Object key) {
		CacheEntry<V> cacheEntry;
		synchronized (internalMap) {
			cacheEntry = internalMap.remove(key);
		}
		if(cacheEntry != null)
			return cacheEntry.data;
		return null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for(Entry<? extends K,? extends V> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void clear() {
		synchronized (internalMap) {
			internalMap.clear();
		}
	}

	@Override
	public Set<K> keySet() {
		synchronized (internalMap) {
			return internalMap.keySet();
		}
	}

	@Override
	public Collection<V> values() {
		synchronized (internalMap) {
			Collection<V> values = new ArrayList<>();
			for (CacheEntry<V> val : internalMap.values()) {
				values.add(val.data);
			}
			return values;
		}
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		synchronized (internalMap) {
			Set<Entry<K,V>> entries = new HashSet<>();
			for (Entry<K,CacheEntry<V>> val : internalMap.entrySet()) {
				entries.add(new AbstractMap.SimpleEntry<>(val.getKey(), val.getValue().data));
			}
			return entries;
		}
	}
}
