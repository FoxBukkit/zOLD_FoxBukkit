/**
 * This file is part of FoxBukkit.
 *
 * FoxBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoxBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FoxBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.doridian.foxbukkit.main.util;

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
