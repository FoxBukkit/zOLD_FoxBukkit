package de.doridian.yiffbukkit.sheep;

import org.bukkit.DyeColor;
import org.bukkit.craftbukkit.entity.CraftSheep;
import org.bukkit.entity.Sheep;
import de.doridian.yiffbukkit.YiffBukkit;

public abstract class AbstractSheep implements Runnable {
	private final YiffBukkit plugin;
	protected final Sheep sheep;
	private final int taskId;

	public AbstractSheep(YiffBukkit plugin, Sheep sheep) {
		this.plugin = plugin;
		this.sheep = sheep;

		taskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 10);
	}

	private final boolean isDead() {
		return ((CraftSheep)sheep).getHandle().dead;
	}

	@Override
	public void run() {
		if (isDead()) {
			plugin.getServer().getScheduler().cancelTask(taskId);
			return;
		}

		final DyeColor newColor = getColor();
		if (newColor != null && newColor != sheep.getColor()) 
			sheep.setColor(newColor);
	}

	public abstract DyeColor getColor();
}
