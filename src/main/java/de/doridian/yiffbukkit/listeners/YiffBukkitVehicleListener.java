package de.doridian.yiffbukkit.listeners;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.vehicle.VehicleListener;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;

import de.doridian.yiffbukkit.YiffBukkit;

public class YiffBukkitVehicleListener extends VehicleListener {
	private YiffBukkit plugin;

	public YiffBukkitVehicleListener(YiffBukkit instance) {
		plugin = instance;

		PluginManager pm = plugin.getServer().getPluginManager();
		pm.registerEvent(Event.Type.VEHICLE_UPDATE, this, Priority.Highest, plugin);
	}

	@Override
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
}
