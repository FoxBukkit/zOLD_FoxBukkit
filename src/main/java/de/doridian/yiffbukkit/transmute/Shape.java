package de.doridian.yiffbukkit.transmute;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import net.minecraft.server.v1_4_R1.DataWatcher;
import net.minecraft.server.v1_4_R1.EntityLiving;
import net.minecraft.server.v1_4_R1.EntityTrackerEntry;
import net.minecraft.server.v1_4_R1.Packet29DestroyEntity;
import net.minecraft.server.v1_4_R1.Packet39AttachEntity;
import net.minecraft.server.v1_4_R1.Packet40EntityMetadata;
import org.bukkit.craftbukkit.v1_4_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.server.Packet;

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
		entityId = entity.getEntityId();
		datawatcher = new DataWatcher();
		datawatcher.a(31, "");
	}

	public void sendPacketToPlayersAround(Packet packet) {
		if (entity instanceof Player)
			transmute.plugin.playerHelper.sendPacketToPlayersAround(entity.getLocation(), 1024, packet, (Player) entity);
		else
			transmute.plugin.playerHelper.sendPacketToPlayersAround(entity.getLocation(), 1024, packet);
	}

	public void deleteEntity() {
		sendPacketToPlayersAround(new Packet29DestroyEntity(entity.getEntityId()));
	}

	public void createOriginalEntity() {
		if (entity instanceof Player)
			YiffBukkit.instance.playerHelper.sendYiffcraftClientCommand((Player) entity, 't', "");

		sendPacketToPlayersAround(transmute.ignorePacket(createOriginalSpawnPacket()));
	}

	private static final Method methodEntityTrackerEntry_b;
	static {
		try {
			methodEntityTrackerEntry_b = EntityTrackerEntry.class.getDeclaredMethod("b");
			methodEntityTrackerEntry_b.setAccessible(true);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	private Packet createOriginalSpawnPacket() {
		final net.minecraft.server.v1_4_R1.Entity notchEntity = ((CraftEntity)entity).getHandle();
		EntityTrackerEntry ete = new EntityTrackerEntry(notchEntity, 0, 0, false);

		try {
			return (Packet) methodEntityTrackerEntry_b.invoke(ete);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
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
		Packet40EntityMetadata p40 = createMetadataPacket(index, value);

		if (entity instanceof Player) {
			sendYCData(index, value);
			transmute.plugin.playerHelper.sendPacketToPlayersAround(entity.getLocation(), 1024, transmute.ignorePacket(p40), (Player) entity);
		}
		else {
			transmute.plugin.playerHelper.sendPacketToPlayersAround(entity.getLocation(), 1024, transmute.ignorePacket(p40));
		}
	}

	public void sendYCData(int index, Object value) {
		if (entity instanceof Player)
			YiffBukkit.instance.playerHelper.sendYiffcraftClientCommand((Player) entity, 'd', index+"|"+value.getClass().getCanonicalName()+"|"+value);
	}

	private Packet40EntityMetadata createMetadataPacket(int index, Object value) {
		try {
			// create entry
			datawatcher.a(index, value.getClass().getConstructor(String.class).newInstance("0"));

			// mark dirty
			datawatcher.watch(index, value.getClass().getConstructor(String.class).newInstance("1"));
		}
		catch (Exception e) { }

		// put the actual data in
		datawatcher.watch(index, value);

		return new Packet40EntityMetadata(entityId, datawatcher, false);
	}

	public abstract void createTransmutedEntity();
	public abstract void createTransmutedEntity(Player forPlayer);

	public abstract void runAction(Player player, String action) throws YiffBukkitCommandException;

	public static Shape getShape(Transmute transmute, Entity entity, String mobType) throws EntityTypeNotFoundException {
		return getShape(transmute, entity, MyEntityTypes.typeNameToClass(mobType));
	}

	public static Shape getShape(Transmute transmute, Entity entity, Class<? extends net.minecraft.server.v1_4_R1.Entity> mobType) throws EntityTypeNotFoundException {
		final int id = MyEntityTypes.classToId(mobType);
		if (EntityLiving.class.isAssignableFrom(mobType)) {
			return getShapeImpl(transmute, entity, id, MobShape.class);
		}

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

		case 10: // Arrow
		case 11: // Snowball
		case 12: // Fireball
		case 13: // SmallFireball
		case 14: // ThrownEnderpearl
		case 15: // EyeOfEnderSignal
		case 16: // ThrownPotion
		//case 17: // ThrownExpBottle
		//case 18: // ItemFrame
		//case 19: // WitherSkull
		case 20: // PrimedTnt
		case 21: // FallingSand
		case 40: // Minecart
		case 41: // Boat
		case 200: // EnderCrystal
		case 1000: // FishingHook
		case 1001: // Potion
		case 1002: // Egg
			return getShapeImpl(transmute, entity, id, VehicleShape.class);

		default:
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
		final net.minecraft.server.v1_4_R1.Entity notchEntity = ((CraftEntity) entity).getHandle();
		net.minecraft.server.v1_4_R1.Entity passenger = notchEntity.passenger;
		net.minecraft.server.v1_4_R1.Entity vehicle = notchEntity.vehicle;

		if (passenger != null)
			transmute.plugin.playerHelper.sendPacketToPlayersAround(entity.getLocation(), 1024, new Packet39AttachEntity(passenger, notchEntity));

		if (vehicle != null)
			transmute.plugin.playerHelper.sendPacketToPlayersAround(entity.getLocation(), 1024, new Packet39AttachEntity(notchEntity, vehicle));
	}

	public abstract boolean onOutgoingPacket(Player ply, int packetID, org.bukkit.event.server.Packet packet);

	public abstract void tick();
}
