package de.doridian.yiffbukkit.main.util;

import net.killa.kept.KeptMap;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ZooKeeperManager {
	private static ZooKeeper zk = null;
	private static ZKMWatcher watcher = null;

	public static class ZKMWatcher implements Watcher {
		@Override
		public void process(WatchedEvent event) {

		}
	}

	public static ZKMWatcher getWatcher() {
		if(watcher == null) {
			watcher = new ZKMWatcher();
		}
		return watcher;
	}

	public static ZooKeeper getZooKeeper() {
		if(zk == null) {
			try {
				zk = new ZooKeeper("127.0.0.1:2181", 20000, getWatcher());
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
		}
		return zk;
	}

	public static Map<String,String> createKeptMap(String name) {
		try {
			name = "/yb/" + name;
			if(zk.exists(name, false) == null) {
				zk.create(name, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
			return new KeptMap(ZooKeeperManager.getZooKeeper(), "/yb/" + name, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		} catch(Exception e) {
			System.out.println("ERROR CONNECTING ZOOKEEPER");
			e.printStackTrace();
			System.out.println("ERROR CONNECTING ZOOKEEPER");
			return new HashMap<String, String>();
		}
	}
}
