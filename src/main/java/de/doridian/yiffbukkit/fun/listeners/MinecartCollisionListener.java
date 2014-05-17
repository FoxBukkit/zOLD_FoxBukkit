package de.doridian.yiffbukkit.fun.listeners;

import de.doridian.yiffbukkit.main.listeners.BaseListener;
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
