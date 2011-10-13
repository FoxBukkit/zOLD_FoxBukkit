package de.doridian.yiffbukkit.transmute;

import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.server.Block;
import net.minecraft.server.DataWatcher;
import net.minecraft.server.EntityArrow;
import net.minecraft.server.EntityBoat;
import net.minecraft.server.EntityEgg;
import net.minecraft.server.EntityFallingSand;
import net.minecraft.server.EntityFireball;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityItem;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntityMinecart;
import net.minecraft.server.EntityPainting;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.EntitySnowball;
import net.minecraft.server.EntityTNTPrimed;
import net.minecraft.server.EntityTypes;
import net.minecraft.server.IAnimal;
import net.minecraft.server.Packet;
import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet21PickupSpawn;
import net.minecraft.server.Packet23VehicleSpawn;
import net.minecraft.server.Packet24MobSpawn;
import net.minecraft.server.Packet25EntityPainting;
import net.minecraft.server.Packet29DestroyEntity;
import net.minecraft.server.Packet39AttachEntity;
import net.minecraft.server.Packet40EntityMetadata;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.util.Utils;

public abstract class Shape {
	final protected Transmute transmute;
	final protected int entityID;
	final protected Player player;
	final protected Entity entity;
	protected DataWatcher datawatcher;

	protected Shape(Transmute transmute, Player player, Entity entity) {
		this.transmute = transmute;
		this.player = player;
		this.entity = entity;
		entityID = entity.getEntityId();
		datawatcher = new DataWatcher();
	}

	public void deleteEntity() {
		if (entity instanceof Player)
			transmute.plugin.playerHelper.sendPacketToPlayersAround(entity.getLocation(), 1024, new Packet29DestroyEntity(entity.getEntityId()), (Player) entity);
		else
			transmute.plugin.playerHelper.sendPacketToPlayersAround(entity.getLocation(), 1024, new Packet29DestroyEntity(entity.getEntityId()));
	}

	public void createOriginalEntity() {
		if (entity instanceof Player)
			transmute.plugin.playerHelper.sendPacketToPlayersAround(entity.getLocation(), 1024, transmute.ignorePacket(createOriginalSpawnPacket()), (Player) entity);
		else
			transmute.plugin.playerHelper.sendPacketToPlayersAround(entity.getLocation(), 1024, transmute.ignorePacket(createOriginalSpawnPacket()));
	}

	private Packet createOriginalSpawnPacket() {
		final net.minecraft.server.Entity notchEntity = ((CraftEntity)entity).getHandle();

		if (notchEntity instanceof EntityItem) {
			EntityItem entityitem = (EntityItem) notchEntity;
			Packet21PickupSpawn packet21pickupspawn = new Packet21PickupSpawn(entityitem);

			/*entityitem.locX = (double) packet21pickupspawn.b / 32.0D;
			entityitem.locY = (double) packet21pickupspawn.c / 32.0D;
			entityitem.locZ = (double) packet21pickupspawn.d / 32.0D;*/
			return packet21pickupspawn;
		} else if (notchEntity instanceof EntityPlayer) {
			return new Packet20NamedEntitySpawn((EntityHuman) notchEntity);
		} else {
			if (notchEntity instanceof EntityMinecart) {
				EntityMinecart entityminecart = (EntityMinecart) notchEntity;

				if (entityminecart.type == 0) {
					return new Packet23VehicleSpawn(notchEntity, 10);
				}

				if (entityminecart.type == 1) {
					return new Packet23VehicleSpawn(notchEntity, 11);
				}

				if (entityminecart.type == 2) {
					return new Packet23VehicleSpawn(notchEntity, 12);
				}
			}

			if (notchEntity instanceof EntityBoat) {
				return new Packet23VehicleSpawn(notchEntity, 1);
			} else if (notchEntity instanceof IAnimal) {
				return new Packet24MobSpawn((EntityLiving) notchEntity);
			/*} else if (notchEntity instanceof EntityFish) {
				return new Packet23VehicleSpawn(notchEntity, 90);*/
			} else if (notchEntity instanceof EntityArrow) {
				net.minecraft.server.Entity entityliving = ((EntityArrow) notchEntity).shooter;
				if(!(entityliving instanceof EntityLiving)) entityliving = null;
				return new Packet23VehicleSpawn(notchEntity, 60, entityliving != null ? entityliving.id : notchEntity.id);
			} else if (notchEntity instanceof EntitySnowball) {
				return new Packet23VehicleSpawn(notchEntity, 61);
			} else if (notchEntity instanceof EntityFireball) {
				EntityFireball entityfireball = (EntityFireball) notchEntity;
				Packet23VehicleSpawn packet23vehiclespawn = new Packet23VehicleSpawn(notchEntity, 63, entityfireball.shooter.id);

				packet23vehiclespawn.e = (int) (entityfireball.motX * 8000.0D);
				packet23vehiclespawn.f = (int) (entityfireball.motY * 8000.0D);
				packet23vehiclespawn.g = (int) (entityfireball.motZ * 8000.0D);
				return packet23vehiclespawn;
			} else if (notchEntity instanceof EntityEgg) {
				return new Packet23VehicleSpawn(notchEntity, 62);
			} else if (notchEntity instanceof EntityTNTPrimed) {
				return new Packet23VehicleSpawn(notchEntity, 50);
			} else {
				if (notchEntity instanceof EntityFallingSand) {
					EntityFallingSand entityfallingsand = (EntityFallingSand) notchEntity;

					if (entityfallingsand.a == Block.SAND.id) {
						return new Packet23VehicleSpawn(notchEntity, 70);
					}

					if (entityfallingsand.a == Block.GRAVEL.id) {
						return new Packet23VehicleSpawn(notchEntity, 71);
					}
				}

				if (notchEntity instanceof EntityPainting) {
					return new Packet25EntityPainting((EntityPainting) notchEntity);
				} else {
					throw new IllegalArgumentException("Don\'t know how to add " + notchEntity.getClass() + "!");
				}
			}
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

		if (entity instanceof Player)
			transmute.plugin.playerHelper.sendPacketToPlayersAround(entity.getLocation(), 1024, transmute.ignorePacket(p40), (Player) entity);
		else
			transmute.plugin.playerHelper.sendPacketToPlayersAround(entity.getLocation(), 1024, transmute.ignorePacket(p40));
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

		return new Packet40EntityMetadata(entityID, datawatcher);
	}

	abstract public void createTransmutedEntity();
	abstract public void createTransmutedEntity(Player forPlayer);

	abstract public void runAction(String action) throws YiffBukkitCommandException;

	public static Shape getShape(Transmute transmute, Player player, Entity entity, String mobType) throws EntityTypeNotFoundException {
		return getShape(transmute, player, entity, typeNameToClass(mobType));
	}

	public static Shape getShape(Transmute transmute, Player player, Entity entity, Class<? extends net.minecraft.server.Entity> mobType) throws EntityTypeNotFoundException {
		return getShape(transmute, player, entity, classToId(mobType));
	}

	public static Shape getShape(Transmute transmute, Player player, Entity entity, int mobType) {
		/*
		 from: "    a\(.*\.class, "(.*)", (.*)\);"
		 to: case \2: // \1
		 */
		switch (mobType) {
		//case 1: // Item
		//case 2: // XPOrb

		//case 9: // Painting
		//case 10: // Arrow
		//case 11: // Snowball

		//case 20: // PrimedTnt
		//case 21: // FallingSand

		//case 40: // Minecart
		//case 41: // Boat
			//return getShapeImpl(transmute, player, entity, mobType, VehicleShape.class);

		//case 48: // Mob
		//case 49: // Monster

		case 50: // Creeper
		case 51: // Skeleton
		case 52: // Spider
		case 53: // Giant
		case 54: // Zombie
		case 55: // Slime
		case 56: // Ghast
		case 57: // PigZombie
		case 58: // Enderman
		case 59: // CaveSpider
		case 60: // Silverfish

		case 90: // Pig
		case 91: // Sheep
		case 92: // Cow
		case 93: // Chicken
		case 94: // Squid
		case 95: // Wolf
			return getShapeImpl(transmute, player, entity, mobType, MobShape.class);
		default:
			throw new RuntimeException("Invalid shape.");
		}
	}

	private static Shape getShapeImpl(Transmute transmute, Player player, Entity entity, int mobType, Class<? extends Shape> shapeClass) {
		try {
			return shapeClass.getConstructor(Transmute.class, Player.class, Entity.class, int.class).newInstance(transmute, player, entity, mobType);
		} catch (Exception e) {
			throw new RuntimeException("Error instantiating shape.", e);
		}
	}

	private static final Class<? extends net.minecraft.server.Entity> typeNameToClass(String mobType) throws EntityTypeNotFoundException {
		Map<String, Class<? extends net.minecraft.server.Entity>> typeNameToClass = Utils.getPrivateValue(EntityTypes.class, null, "a");

		for (Entry<String, Class<? extends net.minecraft.server.Entity>> entry : typeNameToClass.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(mobType))
				return entry.getValue();
		}

		throw new EntityTypeNotFoundException();
		//return typeNameToClass.get(mobType);
	}
	private static final int classToId(Class<? extends net.minecraft.server.Entity> mobType) throws EntityTypeNotFoundException {
		Map<Class<? extends net.minecraft.server.Entity>, Integer> classToId = Utils.getPrivateValue(EntityTypes.class, null, "d");

		final Integer id = classToId.get(mobType);
		if (id == null)
			throw new EntityTypeNotFoundException();

		return id;
	}

	public void reattachPassenger() {
		final net.minecraft.server.Entity notchEntity = ((CraftEntity) entity).getHandle();
		net.minecraft.server.Entity passenger = notchEntity.passenger;
		net.minecraft.server.Entity vehicle = notchEntity.vehicle;

		if (passenger != null)
			transmute.plugin.playerHelper.sendPacketToPlayersAround(entity.getLocation(), 1024, new Packet39AttachEntity(passenger, notchEntity));

		if (vehicle != null)
			transmute.plugin.playerHelper.sendPacketToPlayersAround(entity.getLocation(), 1024, new Packet39AttachEntity(notchEntity, vehicle));
	}
}
