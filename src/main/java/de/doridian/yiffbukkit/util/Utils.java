package de.doridian.yiffbukkit.util;

import java.lang.reflect.Field;

import net.minecraft.server.EntityFallingSand;
import net.minecraft.server.EntityPig;
import net.minecraft.server.EntityTNTPrimed;
import net.minecraft.server.EntityWolf;
import net.minecraft.server.WorldServer;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftFallingSand;
import org.bukkit.craftbukkit.entity.CraftTNTPrimed;
import org.bukkit.craftbukkit.entity.CraftWolf;
import org.bukkit.entity.Boat;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Wolf;
import org.bukkit.util.Vector;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.sheep.CamoSheep;
import de.doridian.yiffbukkit.sheep.PartySheep;

public class Utils {
	private YiffBukkit plugin;
	public Utils(YiffBukkit iface) {
		plugin = iface;
	}

	public static String concatArray(String[] array, int start, String def) {
		if(array.length <= start) return def;
		if(array.length <= start + 1) return array[start];
		String ret = array[start];
		for(int i=start+1;i<array.length;i++) {
			ret += " " + array[i];
		}
		return ret;
	}

	public static String SerializeLocation(Location loc) {
		return loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getYaw() + ";" + loc.getPitch() + ";" + loc.getWorld().getName() + ";" + loc.getWorld().getEnvironment().name();
	}

	public Location UnserializeLocation(String str) {
		String[] split = str.split(";");
		return new Location(plugin.GetOrCreateWorld(split[5], Environment.valueOf(split[6])), Double.valueOf(split[0]), Double.valueOf(split[1]), Double.valueOf(split[2]), Float.valueOf(split[3]), Float.valueOf(split[4]));
	}

	@SuppressWarnings("unchecked")
	public static <T, E> T getPrivateValue(Class<? super E> class1, E instance, String field) {
		try
		{
			Field f = class1.getDeclaredField(field);
			f.setAccessible(true);
			return (T) f.get(instance);
		} catch (Exception e) {
			return null;
		}
	}

	public static <T, E> void setPrivateValue(Class<? super T> instanceclass, T instance, String field, E value) {
		try
		{
			Field field_modifiers = Field.class.getDeclaredField("modifiers");
			field_modifiers.setAccessible(true);


			Field f = instanceclass.getDeclaredField(field);
			int modifiers = field_modifiers.getInt(f);
			if ((modifiers & 0x10) != 0)
				field_modifiers.setInt(f, modifiers & 0xFFFFFFEF);
			f.setAccessible(true);
			f.set(instance, value);
		} catch (Exception e) { }
	}

	static String[] directions = { "N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW" };
	public static String yawToDirection(double yaw) {
		yaw = (yaw%360+630)%360;

		int intdeg = (int) Math.round(yaw / 22.5F);
		if (intdeg < 0) intdeg += 16;
		if (intdeg >= 16) intdeg -= 16;

		return directions[intdeg];
	}

	public static double vectorToYaw(Vector offset) {
		return Math.toDegrees(Math.atan2(-offset.getX(), offset.getZ()));
	}
	
	public Entity buildMob(final String[] types, Player player, Player them, Location location) throws YiffBukkitCommandException {
		Entity previous = null;
		final World world = player.getWorld();
		final WorldServer notchWorld = ((CraftWorld)world).getHandle();
		Entity first = null;
		for (String part : types) {
			String[] partparts = part.split(":");

			String type = partparts[0];
			String data = partparts.length >= 2 ? partparts[1] : null;

			Entity entity;
			if (type.equals("ME")) {
				entity = player;
			}
			else if (type.equals("THEM")) {
				entity = them;
			}
			else if (type.equals("TNT")) {
				EntityTNTPrimed notchEntity = new EntityTNTPrimed(notchWorld, location.getX(), location.getY(), location.getZ());
				notchWorld.a(notchEntity);

				entity = new CraftTNTPrimed((CraftServer)plugin.getServer(), notchEntity);
			}
			else if(type.equals("SAND") || type.equals("GRAVEL")) {
				int material = Material.valueOf(type).getId();
				EntityFallingSand notchEntity = new EntityFallingSand(notchWorld, location.getX(), location.getY(), location.getZ(), material);
				//EntityTNTPrimed notchEntity = new EntityTNTPrimed(notchWorld, location.getX(), location.getY(), location.getZ());
				notchWorld.a(notchEntity);

				entity = new CraftFallingSand((CraftServer)plugin.getServer(), notchEntity);
			}
			else if (type.equals("MINECART") || type.equals("CART")) {
				entity = world.spawnMinecart(location);
			}
			else if (type.equals("BOAT")) {
				entity = world.spawnBoat(location);
			}
			else if (type.equals("THIS")) {

				Vector eyeVector = location.getDirection().clone();
				Vector eyeOrigin = location.toVector().clone();

				entity = null;
				for (Entity currentEntity : player.getWorld().getEntities()) {
					Location eyeLocation;
					if (currentEntity instanceof LivingEntity) {
						eyeLocation = ((LivingEntity)currentEntity).getEyeLocation();
					}
					else if (currentEntity instanceof Boat || currentEntity instanceof Minecart) {
						eyeLocation = currentEntity.getLocation();
					}
					else {
						continue;
					}

					Vector pos = eyeLocation.toVector().clone();
					pos.add(new Vector(0, 0.6, 0));

					pos.subtract(eyeOrigin);

					if (pos.lengthSquared() > 9)
						continue;

					double dot = pos.clone().normalize().dot(eyeVector);

					if (dot < 0.8)
						continue;


					if (currentEntity.equals(player))
						continue;

					entity = currentEntity;
					break;
				}
				if (entity == null) {
					throw new YiffBukkitCommandException("You must face a creature/boat/minecart");
				}

			}
			else if (type.equals("SLIME")) {
				entity = world.spawnCreature(location, CreatureType.WOLF);
				final Slime slime = (Slime)entity;
				
				if (data != null) {
					
					try {
						int size = Integer.parseInt(data);
						slime.setSize(size);
					} catch (NumberFormatException e) { }
					
				}
			}
			else if (type.equals("WOLF")) {
				entity = world.spawnCreature(location, CreatureType.WOLF);
				final Wolf wolf = (Wolf)entity;

				if (data != null) { 
					for (String subData : data.split(",")) {
						if (subData.isEmpty())
							continue;

						if (subData.equals("ANGRY")) {
							wolf.setAngry(true);
						}
						else if (subData.equals("SITTING") || subData.equals("SIT")) {
							wolf.setSitting(true);
						}
						else if (subData.equals("TAME") || subData.equals("TAMED")) {
							CraftWolf craftWolf = (CraftWolf) wolf;
							EntityWolf eWolf = craftWolf.getHandle();
							eWolf.d(true);
							if (them == null)
								eWolf.a(player.getName());
							else
								eWolf.a(them.getName());
						}
					}
				}
			}
			else if (type.equals("SHEEP")) {
				entity = world.spawnCreature(location, CreatureType.SHEEP);
				final Sheep sheep = (Sheep)entity;

				if ("CAMO".equals(data) || "CAMOUFLAGE".equals(data)) {
					new CamoSheep(plugin, sheep);
				}
				else if ("PARTY".equals(data)) {
					new PartySheep(plugin, sheep);
				}
				else {
					DyeColor dyeColor = DyeColor.WHITE;
					try {
						if ("RAINBOW".equals(data) || "RAINBOWS".equals(data) || "RANDOM".equals(data)) {
							DyeColor[] dyes = DyeColor.values();
							dyeColor = dyes[(int)Math.floor(dyes.length*Math.random())];
						}
						else {
							dyeColor = DyeColor.valueOf(data);
						}
					}
					catch (Exception e) { }

					sheep.setColor(dyeColor);
				}
			}
			else {
				try {
					CreatureType creatureType = CreatureType.valueOf(type);
					entity = world.spawnCreature(location, creatureType);
				}
				catch (IllegalArgumentException e) {
					throw new YiffBukkitCommandException("Creature type "+type+" not found", e);
				}
			}

			if (entity == null)
				throw new YiffBukkitCommandException("Failed to spawn "+type);

			if (previous == null) {
				first = entity;
			}
			else {
				net.minecraft.server.Entity eCreature = ((CraftEntity)entity).getHandle();
				net.minecraft.server.Entity ePrevious = ((CraftEntity)previous).getHandle();
				if (ePrevious instanceof EntityPig)
					((EntityPig)ePrevious).a(true);

				eCreature.setPassengerOf(ePrevious);
			}

			previous = entity;
		}
		if (first == null) {
			throw new YiffBukkitCommandException("Unknown error occured while spawning entity.");
		}
		return first;
	}}
