package de.doridian.yiffbukkit.advanced.listeners;

import de.doridian.yiffbukkit.main.listeners.BaseListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.WorldLoadEvent;

public class ServerRestartedListener extends BaseListener {
	private boolean initialized = false;

	@EventHandler
	public void onWorldLoaded(WorldLoadEvent event) {
		initializeMsg();
	}

	private void initializeMsg() {
		if(initialized) return;
		initialized = true;

		try {
			Runtime.getRuntime().exec("./server_online.sh");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
