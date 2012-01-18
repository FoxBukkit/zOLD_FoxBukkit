package de.doridian.yiffbukkit.listeners;

import de.doridian.yiffbukkit.YiffBukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.util.Vector;

public class YiffBukkitVehicleListener implements Listener {
	private YiffBukkit plugin;

	public YiffBukkitVehicleListener(YiffBukkit instance) {
		plugin = instance;

		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(event = VehicleUpdateEvent.class, priority = EventPriority.HIGHEST)
	public void onVehicleUpdate(VehicleUpdateEvent event) {
		final Vehicle vehicle = event.getVehicle();

		if (vehicle.getWorld().getBlockTypeIdAt(vehicle.getLocation()) != 27) // powered rail
			return;

		if (!(vehicle instanceof Minecart))
			return;

		final World world = vehicle.getWorld();

		final Location location = vehicle.getLocation();
		final int blockX = location.getBlockX();
		final int blockY = location.getBlockY();
		final int blockZ = location.getBlockZ();

		final byte poweredRailData = world.getBlockAt(blockX, blockY, blockZ).getData();
		if ((poweredRailData & 0x8) == 0)
			return;

		Block blockBelow = world.getBlockAt(blockX, blockY-1, blockZ);
		if (blockBelow.getTypeId() != 35) // wool
			return;

		final Vector velocity = vehicle.getVelocity();
		double motionX = velocity.getX();
		final double motionY = velocity.getY();
		double motionZ = velocity.getZ();
		if (motionX > 0.0001 || motionX < -0.0001 || motionY > 0.0001 || motionY < -0.0001 || motionZ > 0.0001 || motionZ < -0.0001)
			return;

		switch (blockBelow.getData()) {
		case 0x4: // yellow
			switch (poweredRailData & 0x7) {
			case 0x0:
				++motionZ;
				break;

			case 0x1:
				++motionX;
				break;

			default:
				return;
			}
			break;

		case 0xE: // red
			switch (poweredRailData & 0x7) {
			case 0x0:
				--motionZ;
				break;

			case 0x1:
				--motionX;
				break;

			default:
				return;
			}
			break;

		default:
			return;
		}

		vehicle.setVelocity(new Vector(motionX, motionY, motionZ));
	}


	@EventHandler(event = VehicleEntityCollisionEvent.class, priority = EventPriority.HIGHEST)
	public void onVehicleEntityCollision(VehicleEntityCollisionEvent event) {
		if (event.isCancelled())
			return;
		
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

		emptyMinecart.remove();
		event.setCancelled(true);
	}

	private boolean checkLiving(Vehicle vehicle, LivingEntity entity) {
		if (entity instanceof Player)
			return false;

		if (vehicle.getPassenger() == null)
			return false;

		entity.damage(entity.getHealth());
		return true;
	}
}
