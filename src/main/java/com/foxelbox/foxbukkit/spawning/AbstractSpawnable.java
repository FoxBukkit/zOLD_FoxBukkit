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
package com.foxelbox.foxbukkit.spawning;

import com.foxelbox.foxbukkit.main.FoxBukkitCommandException;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;

public abstract class AbstractSpawnable<V extends Entity> implements Spawnable<V> {
	protected V entity;
	private boolean isSpawned = false;

	@Override
	public V getEntity() throws FoxBukkitCommandException {
		ensureSpawned();
		return entity;
	}

	@Override
	public net.minecraft.server.v1_7_R3.Entity getInternalEntity() throws FoxBukkitCommandException {
		ensureSpawned();
		return ((CraftEntity) entity).getHandle();
	}

	private void ensureSpawned() throws FoxBukkitCommandException {
		if (isSpawned)
			return;

		isSpawned = true;
		spawn();

		if (entity == null)
			throw new FoxBukkitCommandException("Failed to spawn entity.");
	}

	protected abstract void spawn() throws FoxBukkitCommandException;
}
