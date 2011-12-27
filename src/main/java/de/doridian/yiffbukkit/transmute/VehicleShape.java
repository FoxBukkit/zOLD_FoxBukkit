package de.doridian.yiffbukkit.transmute;

import net.minecraft.server.MathHelper;
import net.minecraft.server.Packet23VehicleSpawn;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.server.Packet;

import java.util.HashMap;
import java.util.Map;

public class VehicleShape extends EntityShape {
	private static final Map<Integer, Integer> mobTypeMap = new HashMap<Integer, Integer>();
	{
		mobTypeMap.put(10, 60); // Arrow
		mobTypeMap.put(11, 61); // Snowball
		mobTypeMap.put(12, 63); // Fireball
		mobTypeMap.put(13, 64); // SmallFireball
		mobTypeMap.put(14, 65); // ThrownEnderpearl
		mobTypeMap.put(15, 72); // EyeOfEnderSignal
		mobTypeMap.put(20, 50); // PrimedTnt
		mobTypeMap.put(21, 70); // FallingSand
		mobTypeMap.put(40, 10); // Minecart
		mobTypeMap.put(41, 1); // Boat
		mobTypeMap.put(200, 51); // EnderCrystal

		// These are not in EntityTypes.class:
		mobTypeMap.put(1000, 90); // FishingHook
		mobTypeMap.put(1001, 73); // Potion
		mobTypeMap.put(1002, 62); // Egg
	}

	private int vehicleType;
	private int subType = 0;

	public VehicleShape(Transmute transmute, Entity entity, int mobType) {
		super(transmute, entity, mobType);

		vehicleType = mobTypeMap.get(mobType);

		switch (mobType) {

		case 10: // Arrow
			yawOffset = 90;
			/* FALL-THROUGH */

		case 11: // Snowball
		case 12: // Fireball
		case 13: // SmallFireball
		case 14: // ThrownEnderpearl
		case 15: // EyeOfEnderSignal
		case 20: // PrimedTnt
		case 21: // FallingSand
		case 1000: // FishingHook
		case 1001: // Potion
			yOffset = 1.62;
			break;

		case 40: // Minecart
		case 41: // Boat
			yawOffset = 270;
			yOffset = 0.5;
			break;
		}

		switch (mobType) {
		case 10: // Arrow
		case 15: // EyeOfEnderSignal
		case 40: // Minecart
		case 41: // Boat
		case 200: // EnderCrystal
		case 1000: // FishingHook
			dropping = true;
			break;

		default:
			dropping = true;
		}
	}

	@Override
	protected Packet createSpawnPacket() {
		final net.minecraft.server.Entity notchEntity = ((CraftEntity) this.entity).getHandle();

		final Packet23VehicleSpawn p23 = new Packet23VehicleSpawn(notchEntity, vehicleType, subType);
		p23.c = MathHelper.floor((notchEntity.locY+yOffset) * 32.0D);

		return p23;
	}

	public int getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(int vehicleType) {
		this.vehicleType = vehicleType;

		deleteEntity();
		createTransmutedEntity();
	}

	public int getSubType() {
		return subType;
	}

	public void setSubType(int subType) {
		this.subType = subType;

		deleteEntity();
		createTransmutedEntity();
	}

	public void setVehicleType(int vehicleType, int subType) {
		this.vehicleType = vehicleType;
		this.subType = subType;

		deleteEntity();
		createTransmutedEntity();
	}
}
