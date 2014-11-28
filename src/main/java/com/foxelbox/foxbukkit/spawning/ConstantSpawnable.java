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
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftEntity;
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
	public V getEntity() throws FoxBukkitCommandException {
		return entity;
	}

	@Override
	public net.minecraft.server.v1_8_R1.Entity getInternalEntity() throws FoxBukkitCommandException {
		return ((CraftEntity) entity).getHandle();
	}
}
