package de.doridian.yiffbukkit.main.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public abstract class ScheduledTask implements Runnable {
	private final Plugin plugin;
	private int taskId = -1;

	public ScheduledTask(Plugin plugin) {
		this.plugin = plugin;
	}


	public void scheduleSyncDelayed(long delay) {
		taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this, delay);
	}


	public void scheduleSyncDelayed() {
		taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this);
	}


	public void scheduleSyncRepeating(long delay, long period) {
		taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, delay, period);
	}


	@Deprecated
	public void scheduleAsyncDelayed(long delay) {
		taskId = Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, this, delay);
	}


	@Deprecated
	public void scheduleAsyncDelayed() {
		taskId = Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, this);
	}

	@Deprecated
	public void scheduleAsyncRepeating(long delay, long period) {
		taskId = Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, this, delay, period);
	}


	public void cancel() {
		Bukkit.getScheduler().cancelTask(taskId);
		taskId = -1;
	}
}
