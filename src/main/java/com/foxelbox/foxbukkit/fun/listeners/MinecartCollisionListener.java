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
package com.foxelbox.foxbukkit.fun.listeners;

import com.foxelbox.foxbukkit.main.listeners.BaseListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.PoweredMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;

public class MinecartCollisionListener extends BaseListener {
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onVehicleEntityCollision(VehicleEntityCollisionEvent event) {
		final Vehicle vehicle = event.getVehicle();

		if (!(vehicle instanceof Minecart))
			return;

		Entity entity = event.getEntity();

		if (!(entity instanceof Minecart)) {
			if (entity instanceof LivingEntity)
				event.setCancelled(checkLiving(vehicle, (LivingEntity)entity));
			return;
		}

		final boolean vehicleFull = vehicle.getPassenger() instanceof Player;
		final boolean entityFull = entity.getPassenger() instanceof Player;

		if (entityFull == vehicleFull)
			return;

		final Entity emptyMinecart = entityFull ? vehicle : entity;

		if (emptyMinecart instanceof PoweredMinecart)
			return;

		if (emptyMinecart instanceof StorageMinecart)
			return;

		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				emptyMinecart.remove();
			}
		});
		event.setCancelled(true);
	}

	private boolean checkLiving(Vehicle vehicle, final LivingEntity entity) {
		if (entity instanceof Player)
			return false;

		if (vehicle.getPassenger() == null)
			return false;

		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				entity.damage(entity.getHealth());
			}
		});
		return true;
	}
}
