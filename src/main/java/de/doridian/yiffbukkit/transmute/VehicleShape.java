package de.doridian.yiffbukkit.transmute;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.server.MathHelper;
import net.minecraft.server.Packet23VehicleSpawn;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class VehicleShape extends MobShape {
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

	public VehicleShape(Transmute transmute, Player player, Entity entity, int mobType) {
		super(transmute, player, entity, mobType);

		vehicleType = mobTypeMap.get(mobType);

		switch (mobType) {
		case 40:
		case 41:
			yawOffset = 270;
			yOffset = 0.5;
			break;

		case 10:
			yawOffset = 90;
			/* FALL-THROUGH */

		case 12:
		case 13:
		case 14:
		case 15:
		case 20:
		case 21:
			yOffset = 1.62;
			break;
		}
	}

	@Override
	protected Packet23VehicleSpawn createSpawnPacket() {
		System.out.println("Creating spawn packet");
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
