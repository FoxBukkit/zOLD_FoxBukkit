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

import com.foxelbox.foxbukkit.main.FoxBukkitCommandException;
import com.foxelbox.foxbukkit.main.util.Utils;
import net.minecraft.server.v1_8_R1.DataWatcher;
import net.minecraft.server.v1_8_R1.EntityLiving;
import net.minecraft.server.v1_8_R1.EntityTrackerEntry;
import net.minecraft.server.v1_8_R1.ItemStack;
import net.minecraft.server.v1_8_R1.Packet;
import net.minecraft.server.v1_8_R1.PacketPlayOutAttachEntity;
import net.minecraft.server.v1_8_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R1.PacketPlayOutEntityMetadata;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class Shape {
	protected final Transmute transmute;
	protected final int entityId;
	protected final Entity entity;
	protected final DataWatcher datawatcher;

	protected Shape(Transmute transmute, Entity entity) {
		this.transmute = transmute;
		this.entity = entity;
		this.entityId = entity.getEntityId();
		this.datawatcher = Utils.createEmptyDataWatcher();
		datawatcher.a(31, ""); // v1_7_R1
	}

	public void sendPacketToPlayersAround(Packet packet) {
		if (entity instanceof Player)
			transmute.plugin.playerHelper.sendPacketToPlayersAround(entity.getLocation(), 1024, packet, (Player) entity);
		else
			transmute.plugin.playerHelper.sendPacketToPlayersAround(entity.getLocation(), 1024, packet);
	}

	public void deleteEntity() {
		sendPacketToPlayersAround(new PacketPlayOutEntityDestroy(entity.getEntityId()));
	}

	public void createOriginalEntity() {
		sendPacketToPlayersAround(transmute.ignorePacket(createOriginalSpawnPacket()));
	}

	private static final Method methodEntityTrackerEntry_getPacketForThisEntity;
	static {
		try {
			methodEntityTrackerEntry_getPacketForThisEntity = EntityTrackerEntry.class.getDeclaredMethod("c"); // v1_7_R1
			methodEntityTrackerEntry_getPacketForThisEntity.setAccessible(true);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	private Packet createOriginalSpawnPacket() {
		final net.minecraft.server.v1_8_R1.Entity notchEntity = ((CraftEntity) entity).getHandle();
		final EntityTrackerEntry ete = new EntityTrackerEntry(notchEntity, 0, 0, false);

		try {
			return (Packet) methodEntityTrackerEntry_getPacketForThisEntity.invoke(ete);
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			if (cause instanceof RuntimeException)
				throw (RuntimeException) cause;

			if (cause instanceof Error)
				throw (Error) cause;

			throw new RuntimeException(cause);
		}
	}

	public byte getDataByte(int index) {
		try {
			return datawatcher.getByte(index);
		}
		catch (NullPointerException e) {
			return 0;
		}
	}

	public int getDataInteger(int index) {
		try {
			return datawatcher.getInt(index);
		}
		catch (NullPointerException e) {
			return 0;
		}
	}

	public String getDataString(int index) {
		try {
			return datawatcher.getString(index);
		}
		catch (NullPointerException e) {
			return null;
		}
	}

	public void setData(int index, Object value) {
		final PacketPlayOutEntityMetadata p40 = createMetadataPacket(index, value);

		if (entity instanceof Player)
			transmute.plugin.playerHelper.sendPacketToPlayersAround(entity.getLocation(), 1024, transmute.ignorePacket(p40), (Player) entity);
		else
			transmute.plugin.playerHelper.sendPacketToPlayersAround(entity.getLocation(), 1024, transmute.ignorePacket(p40));
	}

	protected PacketPlayOutEntityMetadata createMetadataPacket(int index, Object value) {
		if (value instanceof ItemStack) {
			try {
				// create entry
				datawatcher.a(index, 5);
			} catch (Exception e) { }

			// put the actual data in
			datawatcher.watch(index, value);

			// mark dirty
			datawatcher.update(index);

			final PacketPlayOutEntityMetadata packet40EntityMetadata = new PacketPlayOutEntityMetadata(entityId, datawatcher, false);
			/* TODO: check if still necessary
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			packet40EntityMetadata.a(new DataOutputStream(baos)); // v1_7_R1
			*/
			return packet40EntityMetadata;
		}
		else {
			try {
				// create entry
				datawatcher.a(index, value.getClass().getConstructor(String.class).newInstance("0")); // v1_7_R1
				// mark dirty
				datawatcher.watch(index, value.getClass().getConstructor(String.class).newInstance("1"));
			}
			catch (Exception e) { }

			// put the actual data in
			datawatcher.watch(index, value);

			return new PacketPlayOutEntityMetadata(entityId, datawatcher, false);
		}
	}

	public abstract void createTransmutedEntity();
	public abstract void createTransmutedEntity(Player forPlayer);

	public abstract void runAction(CommandSender commandSender, String action) throws FoxBukkitCommandException;

	public static Shape getShape(Transmute transmute, Entity entity, String mobType) throws EntityTypeNotFoundException {
		return getShape(transmute, entity, MyEntityTypes.typeNameToClass(mobType));
	}

	public static Shape getShape(Transmute transmute, Entity entity, Class<? extends net.minecraft.server.v1_8_R1.Entity> mobType) throws EntityTypeNotFoundException {
		final int id = MyEntityTypes.classToId(mobType);

		/*
		 from: "    a\(.*\.class, "(.*)", (.*)\);"
		 to: case \2: // \1
		 */
		switch (id) {
		case 1: // Item
			return getShapeImpl(transmute, entity, id, ItemShape.class);

		case 2: // XPOrb
			return getShapeImpl(transmute, entity, id, ExperienceOrbShape.class);

		case 9: // Painting
			return getShapeImpl(transmute, entity, id, PaintingShape.class);

		case 18: // ItemFrame
			return getShapeImpl(transmute, entity, id, ItemFrameShape.class);

		case 8: // Leash
		case 10: // Arrow
		case 11: // Snowball
		case 12: // Fireball
		case 13: // SmallFireball
		case 14: // ThrownEnderpearl
		case 15: // EyeOfEnderSignal
		case 16: // ThrownPotion
		//case 17: // ThrownExpBottle
		//case 19: // WitherSkull
		case 20: // PrimedTnt
		case 21: // FallingSand
		case 22: // FireworksRocketEntity
		case 40: // Minecart
		case 41: // Boat
		case 48: // Mob
		case 49: // Monster
		case 200: // EnderCrystal
		case 1000: // FishingHook
		case 1001: // Potion
		case 1002: // Egg
			return getShapeImpl(transmute, entity, id, VehicleShape.class);

		default:
			if (EntityLiving.class.isAssignableFrom(mobType)) {
				return getShapeImpl(transmute, entity, id, MobShape.class);
			}

			throw new RuntimeException("Invalid shape.");
		}
	}

	public static Shape getShape(Transmute transmute, Entity entity, int mobType) throws EntityTypeNotFoundException {
		return getShape(transmute, entity, MyEntityTypes.idToClass(mobType));
	}

	private static Shape getShapeImpl(Transmute transmute, Entity entity, int mobType, Class<? extends Shape> shapeClass) {
		try {
			return shapeClass.getConstructor(Transmute.class, Entity.class, int.class).newInstance(transmute, entity, mobType);
		} catch (Exception e) {
			throw new RuntimeException("Error instantiating shape.", e);
		}
	}


	public void reattachPassenger() {
		final net.minecraft.server.v1_8_R1.Entity notchEntity = ((CraftEntity) entity).getHandle();
		final net.minecraft.server.v1_8_R1.Entity passenger = notchEntity.passenger;
		final net.minecraft.server.v1_8_R1.Entity vehicle = notchEntity.vehicle;

		if (passenger != null)
			transmute.plugin.playerHelper.sendPacketToPlayersAround(entity.getLocation(), 1024, new PacketPlayOutAttachEntity(0, passenger, notchEntity)); //TODO: Check what this int is for in ctor PacketPlayOutAttachEntity

		if (vehicle != null)
			transmute.plugin.playerHelper.sendPacketToPlayersAround(entity.getLocation(), 1024, new PacketPlayOutAttachEntity(0, notchEntity, vehicle)); //TODO: See above
	}

	public abstract boolean onOutgoingPacket(Player ply, int packetID, Packet packet);

	public abstract void tick();
}
