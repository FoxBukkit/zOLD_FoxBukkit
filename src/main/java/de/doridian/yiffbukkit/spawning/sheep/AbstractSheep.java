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
