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
		mobTypeMap.put(40, 10);
		mobTypeMap.put(41, 1);
	}

	private int vehicleType;
	private int subType = 0;

	public VehicleShape(Transmute transmute, Player player, Entity entity, int mobType) {
		super(transmute, player, entity, mobType);
		yawOffset = 270;
		yOffset = 0.5;

		vehicleType = mobTypeMap.get(mobType);
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
