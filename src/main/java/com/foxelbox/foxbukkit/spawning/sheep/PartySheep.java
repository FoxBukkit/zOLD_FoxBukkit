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
package com.foxelbox.foxbukkit.spawning.sheep;

import com.foxelbox.foxbukkit.core.FoxBukkit;
import org.bukkit.DyeColor;
import org.bukkit.entity.Sheep;

public class PartySheep extends AbstractSheep {
	public PartySheep(FoxBukkit plugin, Sheep sheep) {
		super(plugin, sheep);
	}

	@Override
	public DyeColor getColor()  {
		final DyeColor[] dyes = DyeColor.values();
		return dyes[(int) Math.floor(dyes.length * Math.random())];
	}
}
