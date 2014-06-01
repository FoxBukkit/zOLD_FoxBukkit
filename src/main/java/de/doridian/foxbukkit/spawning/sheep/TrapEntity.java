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
package de.doridian.foxbukkit.spawning.sheep;

import de.doridian.foxbukkit.core.FoxBukkit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.util.HashSet;
import java.util.Set;

public class TrapEntity implements Runnable {
	public static class TrapSheepEntityListener implements Listener {
		public TrapSheepEntityListener() {
			Bukkit.getPluginManager().registerEvents(this, FoxBukkit.instance);
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onEntityDamage(EntityDamageEvent event) {
			if (event.getCause() != DamageCause.ENTITY_ATTACK)
				return;

			if (!trapEntities.contains(event.getEntity()))
				return;

			final Entity entity = event.getEntity();

			entity.getWorld().strikeLightning(entity.getLocation());
		}
	}

	protected final Entity entity;

	private static Set<Entity> trapEntities = new HashSet<>();
	private static Listener entityListener = null;
	private final int taskId;

	public TrapEntity(FoxBukkit plugin, Entity entity) {
		this.entity = entity;

		trapEntities.add(entity);

		taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 200);

		if (entityListener == null) {
			entityListener = new TrapSheepEntityListener();
		}
	}

	@Override
	public void run() {
		if (canBeRemoved()) {
			Bukkit.getScheduler().cancelTask(taskId);
			trapEntities.remove(entity);
		}
	}

	private boolean canBeRemoved() {
		if (!entity.isValid())
			return true;

		//noinspection SimplifiableIfStatement
		if (!(entity instanceof Sheep))
			return false;

		return ((Sheep) entity).isSheared();
	}
}
