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
import org.bukkit.entity.Entity;

public interface Spawnable<V extends Entity> {
	V getEntity() throws FoxBukkitCommandException;
	net.minecraft.server.v1_8_R2.Entity getInternalEntity() throws FoxBukkitCommandException;
}
