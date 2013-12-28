package de.doridian.yiffbukkit.spawning.sheep;

import de.doridian.yiffbukkit.core.YiffBukkit;
import de.doridian.yiffbukkit.main.util.ScheduledTask;
import org.bukkit.DyeColor;
import org.bukkit.entity.Sheep;

public abstract class AbstractSheep extends ScheduledTask {
	protected final Sheep sheep;

	public AbstractSheep(YiffBukkit plugin, Sheep sheep) {
		super(plugin);
		this.sheep = sheep;

		scheduleSyncRepeating(0, 10);
	}

	@Override
	public void run() {
		if (sheep.isDead() || sheep.isSheared()) {
			cancel();
			return;
		}

		final DyeColor newColor = getColor();
		if (newColor != null && newColor != sheep.getColor()) 
			sheep.setColor(newColor);
	}

	public abstract DyeColor getColor();
}
