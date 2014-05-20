/**
 * This file is part of YiffBukkit.
 *
 * YiffBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * YiffBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with YiffBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
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
