package de.doridian.yiffbukkit.spawning;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;

public class ConstantSpawnable<V extends Entity> implements Spawnable<V> {
	private final V entity;
	private boolean isSpawned = false;

	public ConstantSpawnable(V entity) {
		this.entity = entity;
	}

	public static <U extends Entity> ConstantSpawnable<U> create(U entity) {
		return new ConstantSpawnable<>(entity);
	}

	@Override
	public V getEntity() throws YiffBukkitCommandException {
		return entity;
	}

	@Override
	public net.minecraft.server.v1_7_R1.Entity getInternalEntity() throws YiffBukkitCommandException {
		return ((CraftEntity) entity).getHandle();
	}
}
