package de.doridian.yiffbukkit.main.listeners;

import de.doridian.yiffbukkit.componentsystem.YBListener;
import de.doridian.yiffbukkit.core.YiffBukkit;
import de.doridian.yiffbukkit.core.util.PlayerHelper;
import org.bukkit.event.Listener;

public abstract class BaseListener implements Listener, YBListener {
	protected final YiffBukkit plugin;
	protected final PlayerHelper playerHelper;

	protected BaseListener() {
		plugin = YiffBukkit.instance;
		playerHelper = plugin.playerHelper;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
}
