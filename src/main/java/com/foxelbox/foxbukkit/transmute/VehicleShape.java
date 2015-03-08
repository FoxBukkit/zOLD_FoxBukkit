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
package com.foxelbox.foxbukkit.transmute;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import net.minecraft.server.v1_8_R2.MathHelper;
import net.minecraft.server.v1_8_R2.Packet;
import net.minecraft.server.v1_8_R2.PacketPlayOutSpawnEntity;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class VehicleShape extends EntityShape {
	static {
		yawOffsets[10] = 90; // Arrow
		yOffsets[10] = 1.62;
		yOffsets[11] = 1.62; // Snowball
		yOffsets[12] = 1.62; // Fireball
		yOffsets[13] = 1.62; // SmallFireball
		yOffsets[14] = 1.62; // ThrownEnderpearl
		yOffsets[15] = 1.62; // EyeOfEnderSignal
		yOffsets[20] = 1.62; // PrimedTnt
		yOffsets[21] = 1.62; // FallingSand
		yOffsets[22] = 1.62; // FireworksRocketEntity
		yawOffsets[40] = 270; // Minecart
		yOffsets[40] = 0.6;
		yawOffsets[41] = 270; // Boat
		yOffsets[41] = 0.6;
		yOffsets[1000] = 1.62; // FishingHook
		yOffsets[1001] = 1.62; // Potion
	}

	private static final TIntIntMap mobTypeMap = new TIntIntHashMap();
	static {
		mobTypeMap.put(8, 77); // LeashKnot
		mobTypeMap.put(10, 60); // Arrow
		mobTypeMap.put(11, 61); // Snowball
		mobTypeMap.put(12, 63); // Fireball
		mobTypeMap.put(13, 64); // SmallFireball
		mobTypeMap.put(14, 65); // ThrownEnderpearl
		mobTypeMap.put(15, 72); // EyeOfEnderSignal
		mobTypeMap.put(20, 50); // PrimedTnt
		mobTypeMap.put(21, 70); // FallingSand
		mobTypeMap.put(22, 76); // FireworksRocketEntity
		mobTypeMap.put(40, 10); // Minecart
		mobTypeMap.put(41, 1); // Boat
		mobTypeMap.put(200, 51); // EnderCrystal
		mobTypeMap.put(1, 2); // Item
		mobTypeMap.put(18, 71); // ItemFrame

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
		case 8: // LeashKnot
		case 10: // Arrow
		case 15: // EyeOfEnderSignal
		case 40: // Minecart
		case 41: // Boat
		case 200: // EnderCrystal
		case 1000: // FishingHook
			dropping = false;
			break;

		default:
			dropping = true;
		}
	}

	@Override
	public void createTransmutedEntity() {
		super.createTransmutedEntity();
	}

	@Override
	protected Packet createSpawnPacket() {
		try {
			final net.minecraft.server.v1_8_R2.Entity notchEntity = ((CraftEntity) this.entity).getHandle();

			final PacketPlayOutSpawnEntity p23 = new PacketPlayOutSpawnEntity(notchEntity, vehicleType, subType);
			p23.c = MathHelper.floor((notchEntity.locY+yOffset) * 32.0D); // v1_7_R1

			return p23;
		}
		catch (ClassCastException e) {
			final PacketPlayOutSpawnEntity p23 = new PacketPlayOutSpawnEntity();
			// copypasta from PacketPlayOutSpawnEntity(nms.Entity, int, int)
			p23.a = entity.getEntityId(); // v1_7_R1

			final Location location = entity.getLocation();
			p23.b = MathHelper.floor(location.getX() * 32.0D); // v1_7_R1
			p23.c = MathHelper.floor((location.getY()+yOffset) * 32.0D); // v1_7_R1
			p23.d = MathHelper.floor(location.getZ() * 32.0D); // v1_7_R1
			p23.h = MathHelper.d(location.getPitch() * 256.0F / 360.0F); // v1_7_R1
			p23.i = MathHelper.d(location.getYaw() * 256.0F / 360.0F); // v1_7_R1
			p23.j = vehicleType; // v1_7_R1
			p23.k = subType; // v1_7_R1
			if (subType > 0) {
				final Vector velocity = entity.getVelocity();
				double d1 = velocity.getX();
				double d2 = velocity.getY();
				double d3 = velocity.getZ();
				double d4 = 3.9D;
				if (d1 < -d4) d1 = -d4;
				if (d2 < -d4) d2 = -d4;
				if (d3 < -d4) d3 = -d4;
				if (d1 > d4) d1 = d4;
				if (d2 > d4) d2 = d4;
				if (d3 > d4) d3 = d4;
				p23.e = (int)(d1 * 8000.0D); // v1_7_R1
				p23.f = (int)(d2 * 8000.0D); // v1_7_R1
				p23.g = (int)(d3 * 8000.0D); // v1_7_R1
			}

			return p23;
		}
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
