package de.doridian.yiffbukkit.transmute;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import net.minecraft.server.v1_5_R3.MathHelper;
import net.minecraft.server.v1_5_R3.Packet;
import net.minecraft.server.v1_5_R3.Packet23VehicleSpawn;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_5_R3.entity.CraftEntity;
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
		mobTypeMap.put(1, 2); //Item
		mobTypeMap.put(18, 71); //Item

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
		case 1: //Item
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
	public void createTransmutedEntity() {
		super.createTransmutedEntity();

		sendYCData(ShapeYCData.VEHICLE_TYPE, vehicleType);
	}

	@Override
	protected Packet createSpawnPacket() {
		try {
			final net.minecraft.server.v1_5_R3.Entity notchEntity = ((CraftEntity) this.entity).getHandle();

			final Packet23VehicleSpawn p23 = new Packet23VehicleSpawn(notchEntity, vehicleType, subType);
			p23.c = MathHelper.floor((notchEntity.locY+yOffset) * 32.0D);

			return p23;
		}
		catch (ClassCastException e) {
			final Packet23VehicleSpawn p23 = new Packet23VehicleSpawn();
			// copypasta from Packet23VehicleSpawn(nms.Entity, int, int)
			p23.a = entity.getEntityId();

			Location location = entity.getLocation();
			p23.b = MathHelper.floor(location.getX() * 32.0D);
			p23.c = MathHelper.floor((location.getY()+yOffset) * 32.0D);
			p23.d = MathHelper.floor(location.getZ() * 32.0D);
			p23.h = MathHelper.d(location.getPitch() * 256.0F / 360.0F);
			p23.i = MathHelper.d(location.getYaw() * 256.0F / 360.0F);
			p23.j = vehicleType;
			p23.k = subType;
			if (subType > 0) {
				final Vector velocity = entity.getVelocity();
				double d0 = velocity.getX();
				double d1 = velocity.getY();
				double d2 = velocity.getZ();
				double d3 = 3.9D;

				if (d0 < -d3) {
					d0 = -d3;
				}

				if (d1 < -d3) {
					d1 = -d3;
				}

				if (d2 < -d3) {
					d2 = -d3;
				}

				if (d0 > d3) {
					d0 = d3;
				}

				if (d1 > d3) {
					d1 = d3;
				}

				if (d2 > d3) {
					d2 = d3;
				}

				p23.e = (int) (d0 * 8000.0D);
				p23.f = (int) (d1 * 8000.0D);
				p23.g = (int) (d2 * 8000.0D);
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
