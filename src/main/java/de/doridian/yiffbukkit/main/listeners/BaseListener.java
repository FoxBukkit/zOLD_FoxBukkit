package de.doridian.yiffbukkit.main.listeners;

import de.doridian.yiffbukkit.componentsystem.YBListener;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import org.bukkit.event.Listener;

public abstract class BaseListener implements Listener, YBListener {
	protected final YiffBukkit plugin;
	protected final PlayerHelper playerHelper;

	public BaseListener() {
		plugin = YiffBukkit.instance;
		playerHelper = plugin.playerHelper;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
}
