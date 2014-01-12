package de.doridian.yiffbukkit.spawning.sheep;

import de.doridian.yiffbukkit.core.YiffBukkit;
import org.bukkit.DyeColor;
import org.bukkit.entity.Sheep;

public class PartySheep extends AbstractSheep {
	public PartySheep(YiffBukkit plugin, Sheep sheep) {
		super(plugin, sheep);
	}

	@Override
	public DyeColor getColor()  {
		final DyeColor[] dyes = DyeColor.values();
		return dyes[(int) Math.floor(dyes.length * Math.random())];
	}
}
