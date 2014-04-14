package de.doridian.yiffbukkit.spawning;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;

public abstract class AbstractSpawnable<V extends Entity> implements Spawnable<V> {
	protected V entity;
	private boolean isSpawned = false;

	@Override
	public V getEntity() throws YiffBukkitCommandException {
		ensureSpawned();
		return entity;
	}

	@Override
	public net.minecraft.server.v1_7_R3.Entity getInternalEntity() throws YiffBukkitCommandException {
		ensureSpawned();
		return ((CraftEntity) entity).getHandle();
	}

	private void ensureSpawned() throws YiffBukkitCommandException {
		if (isSpawned)
			return;

		isSpawned = true;
		spawn();

		if (entity == null)
			throw new YiffBukkitCommandException("Failed to spawn entity.");
	}

	protected abstract void spawn() throws YiffBukkitCommandException;
}
