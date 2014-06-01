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
package de.doridian.foxbukkit.spawning;

import de.doridian.foxbukkit.advanced.listeners.FoxBukkitHeadChopOffListener;
import de.doridian.foxbukkit.advanced.listeners.FoxBukkitPermaFlameListener;
import de.doridian.foxbukkit.core.FoxBukkit;
import de.doridian.foxbukkit.core.util.PlayerHelper;
import de.doridian.foxbukkit.main.FoxBukkitCommandException;
import de.doridian.foxbukkit.main.PermissionDeniedException;
import de.doridian.foxbukkit.main.commands.system.ICommand;
import de.doridian.foxbukkit.main.util.Utils;
import de.doridian.foxbukkit.spawning.effects.system.EffectProperties;
import de.doridian.foxbukkit.spawning.effects.system.FBEffect;
import de.doridian.foxbukkit.spawning.fakeentity.FakeEntity;
import de.doridian.foxbukkit.spawning.fakeentity.FakeEntityParticleSpawner;
import de.doridian.foxbukkit.spawning.fakeentity.FakeExperienceOrb;
import de.doridian.foxbukkit.spawning.fakeentity.FakeShapeBasedEntity;
import de.doridian.foxbukkit.spawning.fakeentity.FakeVehicle;
import de.doridian.foxbukkit.spawning.potions.AreaCustomPotion;
import de.doridian.foxbukkit.spawning.potions.CustomPotion;
import de.doridian.foxbukkit.spawning.sheep.CamoSheep;
import de.doridian.foxbukkit.spawning.sheep.PartySheep;
import de.doridian.foxbukkit.spawning.sheep.TrapEntity;
import de.doridian.foxbukkit.transmute.ItemShape;
import net.minecraft.server.v1_7_R3.EntityFallingBlock;
import net.minecraft.server.v1_7_R3.EntityLargeFireball;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.EntitySnowball;
import net.minecraft.server.v1_7_R3.EntityTNTPrimed;
import net.minecraft.server.v1_7_R3.Items;
import net.minecraft.server.v1_7_R3.MovingObjectPosition;
import net.minecraft.server.v1_7_R3.NBTTagCompound;
import net.minecraft.server.v1_7_R3.NBTTagList;
import net.minecraft.server.v1_7_R3.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_7_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R3.inventory.CraftItemStack;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class SpawnUtils {
	private final FoxBukkit plugin;
	private final Entity noErrorPlz;

	public SpawnUtils(FoxBukkit plugin) {
		this.plugin = plugin;
		this.noErrorPlz = new FakeEntityParticleSpawner(new Location(null, 0, 0, 0), new Vector(), 0, 0, "");
	}

	public static void logSpawn(String playerName, Location location, int amount, String typeName) {
		final double x = location.getX();
		final double y = location.getY();
		final double z = location.getZ();
		System.out.println(String.format("%s spawned %d %s at (%s,%.0f,%.0f,%.0f)", playerName, amount, typeName, location.getWorld().getName(), x, y, z));
	}

	public Entity buildMob(final String[] types, final CommandSender commandSender, Player them, final Location location) throws FoxBukkitCommandException {
		final Map<String, Spawnable<? extends Entity>> fixedSpawnables = new HashMap<>();

		final AbstractSpawnable<Entity> thisSpawnable = new AbstractSpawnable<Entity>() {
			@Override
			protected void spawn() throws FoxBukkitCommandException {
				final Vector eyeVector = location.getDirection();
				final Vector eyeOrigin = location.toVector();

				for (Entity currentEntity : location.getWorld().getEntities()) {
					final Location eyeLocation;
					if (currentEntity instanceof LivingEntity) {
						eyeLocation = ((LivingEntity) currentEntity).getEyeLocation();
					} else if (currentEntity instanceof Boat || currentEntity instanceof Minecart) {
						eyeLocation = currentEntity.getLocation();
					} else {
						continue;
					}

					final Vector pos = eyeLocation.toVector();
					pos.add(new Vector(0, 0.6, 0));

					pos.subtract(eyeOrigin);

					if (pos.lengthSquared() > 9)
						continue;

					final double dot = pos.clone().normalize().dot(eyeVector);

					if (dot < 0.8)
						continue;


					if (currentEntity.equals(commandSender))
						continue;

					entity = currentEntity;
					return;
				}

				throw new FoxBukkitCommandException("You must face a creature/boat/minecart");
			}
		};
		fixedSpawnables.put("this", thisSpawnable);
		fixedSpawnables.put("thisvehicle", new AbstractSpawnable<Entity>() {
			@Override
			protected void spawn() throws FoxBukkitCommandException {
				entity = thisSpawnable.getEntity().getVehicle();
			}
		});
		fixedSpawnables.put("thisvehicle", new AbstractSpawnable<Entity>() {
			@Override
			protected void spawn() throws FoxBukkitCommandException {
				entity = thisSpawnable.getEntity().getPassenger();
			}
		});

		fixedSpawnables.put("me", new AbstractSpawnable<Player>() {
			@Override
			protected void spawn() throws FoxBukkitCommandException {
				entity = ICommand.asPlayer(commandSender);
			}
		});
		fixedSpawnables.put("mevehicle", new AbstractSpawnable<Entity>() {
			@Override
			protected void spawn() throws FoxBukkitCommandException {
				entity = ICommand.asPlayer(commandSender).getVehicle();
			}
		});
		fixedSpawnables.put("mepassenger", new AbstractSpawnable<Entity>() {
			@Override
			protected void spawn() throws FoxBukkitCommandException {
				entity = ICommand.asPlayer(commandSender).getPassenger();
			}
		});

		if (them != null) {
			fixedSpawnables.put("them", ConstantSpawnable.create(them));
			fixedSpawnables.put("themvehicle", ConstantSpawnable.create(them.getVehicle()));
			fixedSpawnables.put("thempassenger", ConstantSpawnable.create(them.getPassenger()));
		}

		Entity previous = null;
		Entity first = null;
		for (String typeDataAttributes : types) {
			final String[] typeDataAttributesParts = typeDataAttributes.split("@");
			final String typeData = typeDataAttributesParts[0];
			final String[] typeDataParts = typeData.split(":", 2);

			final String type = typeDataParts[0];
			final String data = typeDataParts.length >= 2 ? typeDataParts[1] : null;

			checkMobSpawn(commandSender, type);

			final Entity entity = spawnSingleMob(commandSender, fixedSpawnables, location, type, data);
			for (int i = 1; i < typeDataAttributesParts.length; i++) {
				final String attribute = typeDataAttributesParts[i].toLowerCase();
				if (FBEffect.effectExists(attribute)) {
					final FBEffect effect = FBEffect.create(attribute, entity);
					effect.forceStart();
					continue;
				}

				switch (attribute) {
				case "leash":
					((LivingEntity) entity).setLeashHolder(them);
					break;

				case "permafire":
				case "permaflame":
					if (!FoxBukkitPermaFlameListener.instance.addPermaFlameEntity(entity))
						FoxBukkitPermaFlameListener.instance.removePermaFlameEntity(entity);
					break;

				case "fakefire":
				case "fakeflame":
					FoxBukkitPermaFlameListener.instance.addPermaFlameEntity(entity);

					final int fakeFireTicks = 100;
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						@Override
						public void run() {
							FoxBukkitPermaFlameListener.instance.removePermaFlameEntity(entity);
						}
					}, fakeFireTicks);
					break;

				case "fire":
				case "flame":
					final int fireTicks = Math.max(20, entity.getMaxFireTicks());
					entity.setFireTicks(entity.getFireTicks() + fireTicks);
					break;

				case "baby":
					if (entity instanceof Ageable) {
						((Ageable) entity).setBaby();
					}
					else if (entity instanceof Zombie) {
						((Zombie) entity).setBaby(true);
					}
					break;

				case "headless":
					FoxBukkitHeadChopOffListener.instance.addChoppedEntity(entity.getEntityId());
					break;

				case "trap":
					new TrapEntity(plugin, entity);
					break;

				case "arrows":
				case "pincushion":
					if (entity instanceof CraftLivingEntity) {
						((CraftLivingEntity) entity).getHandle().p(127); // v1_7_R1
					}
					break;
				}
			}

			if (entity == null)
				throw new FoxBukkitCommandException("Failed to spawn "+type);

			if (previous == null) {
				first = entity;
			}
			else {
				if (previous instanceof Pig)
					((Pig)previous).setSaddle(true);

				entity.teleport(location);
				if (previous == entity)
					throw new FoxBukkitCommandException("Cannot attach entities to themselves for now. Sorry.");

				previous.setPassenger(entity);
			}

			if (entity == noErrorPlz)
				continue;

			previous = entity;
		}

		if (first == null)
			throw new FoxBukkitCommandException("Unknown error occured while spawning entity.");

		return first;
	}

	private Entity spawnSingleMob(final CommandSender commandSender,
			Map<String, ? extends Spawnable<? extends Entity>> fixedSpawnables,
			Location location, String type,
			final String data)
			throws FoxBukkitCommandException {
		type = type.toLowerCase();
		final World world = location.getWorld();
		final WorldServer notchWorld = ((CraftWorld)world).getHandle();

		if (type.equals("lightning") || (type.equals("potion") && "LIGHTNING".equalsIgnoreCase(data))) {
			final EntityPlayer notchPlayer = ((CraftPlayer) commandSender).getHandle();

			final net.minecraft.server.v1_7_R3.Entity notchEntity = new CustomPotion(location, 10, notchPlayer) {
				@Override
				protected boolean hit(MovingObjectPosition movingobjectposition) {
					org.bukkit.World world = getBukkitEntity().getWorld();
					world.strikeLightning(new Location(world, this.locX, this.locY, this.locZ));
					return true;
				}
			};

			notchWorld.addEntity(notchEntity);
			return notchEntity.getBukkitEntity();
		}

		final Spawnable<? extends Entity> spawnable = fixedSpawnables.get(type.toLowerCase());
		if (spawnable != null)
			return spawnable.getEntity();

		@SuppressWarnings("unchecked")
		final Spawnable<Player> them = (Spawnable<Player>) fixedSpawnables.get("them");

		switch (type) {
		case "fireball":
			final EntityPlayer playerEntity;
			if (them == null)
				playerEntity = ICommand.asNotchPlayer(commandSender, null);
			else
				playerEntity = (EntityPlayer) them.getInternalEntity();

			final Vector dir = playerEntity.getBukkitEntity().getLocation().getDirection();
			final double dx = dir.getX();
			final double dy = dir.getY();
			final double dz = dir.getZ();

			final EntityLargeFireball notchEntity = new EntityLargeFireball(notchWorld, playerEntity, dx, dy, dz);
			notchEntity.locX = location.getX();
			notchEntity.locY = location.getY();
			notchEntity.locZ = location.getZ();

			final double d3 = 0.1D / Math.sqrt(dx * dx + dy * dy + dz * dz);

			notchEntity.dirX = dx * d3;
			notchEntity.dirY = dy * d3;
			notchEntity.dirZ = dz * d3;

			notchWorld.addEntity(notchEntity);
			return noErrorPlz;//notchEntity.getBukkitEntity();

		case "tnt":
			final EntityTNTPrimed notchTNT = new EntityTNTPrimed(notchWorld, 0, 1, 0, ICommand.asNotchPlayer(commandSender, null));

			notchWorld.addEntity(notchTNT);
			final Entity entity = notchTNT.getBukkitEntity();

			entity.teleport(location);

			return entity;

		case "sand":
		case "gravel":
		case "anvil":
		case "block":
			int typeId;
			final int dataValue;
			if (type.equals("block")) {
				final String[] parts = data.split(":", 2);
				final String typeIdString = parts[0];
				try {
					typeId = Integer.parseInt(typeIdString.toUpperCase());
				}
				catch (NumberFormatException e) {
					try {
						typeId = Material.valueOf(typeIdString.toUpperCase()).getId();
					} catch (IllegalArgumentException e2) {
						return null;
					}
				}
				if (parts.length == 1) {
					dataValue = 0;
				}
				else {
					final String dataValueString = parts[1];
					dataValue = Integer.parseInt(dataValueString.toUpperCase());
				}
			}
			else if (data == null) {
				typeId = Material.valueOf(type.toUpperCase()).getId();
				dataValue = 0;
			}
			else {
				typeId = 1;
				dataValue = 0;
			}

			if (typeId <= 0 || typeId >= 256)
				return null;

			final EntityFallingBlock notchFallingBlock = Utils.spawnFallingBlock(location, typeId, dataValue);
			return notchFallingBlock.getBukkitEntity();

		case "potion":
			final EntityPlayer notchPlayer = ICommand.asNotchPlayer(commandSender, null);

			final String[] parts = data.split(":");

			switch (parts[0].toLowerCase()) {
			case "ninja": {
				final CustomPotion notchPotion = new CustomPotion(location, 8, notchPlayer) {
					@Override
					protected boolean hit(MovingObjectPosition movingobjectposition) throws FoxBukkitCommandException {
						final Entity thisBukkitEntity = getBukkitEntity();
						final World world = thisBukkitEntity.getWorld();
						world.playEffect(new Location(world, this.locX, this.locY, this.locZ), Effect.POTION_BREAK, potionId);

						plugin.playerHelper.vanish(ICommand.asPlayer(commandSender));

						return true;
					}
				};

				notchWorld.addEntity(notchPotion);
				return notchPotion.getBukkitEntity();
			}

			case "meteor":
				final double radius;
				if (parts.length < 2) {
					radius = 3;
				}
				else {
					radius = Math.min(8, Double.parseDouble(parts[1]));
				}
				final double speed;
				if (parts.length < 3) {
					speed = 1;
				}
				else {
					speed = Math.min(1.5, Double.parseDouble(parts[2]));
				}

				final Meteor notchMeteor = new Meteor(location, notchPlayer, radius, speed);

				notchWorld.addEntity(notchMeteor);
				return notchMeteor.getBukkitEntity();

			default:
				int potionId = -1;
				try {
					potionId = Integer.parseInt(data);
				}
				catch (NumberFormatException ignored) { }

				if (potionId == -1) {
					final EffectProperties effectProperties = FBEffect.getEffectProperties(data.toLowerCase());
					if (effectProperties == null)
						throw new FoxBukkitCommandException("Effect '"+data+"' does not exist");

					final CustomPotion notchPotion = new AreaCustomPotion(location, effectProperties.potionColor(), notchPlayer, effectProperties.radius()) {
						@Override
						protected void areaHit(final Entity entity) {
							try {
								FBEffect.create(data.toLowerCase(), entity).start();
							} catch (FoxBukkitCommandException ignored) {
							}
						}
					};

					notchWorld.addEntity(notchPotion);
					final Entity bukkitPotion = notchPotion.getBukkitEntity();

					FBEffect.createTrail(data.toLowerCase(), bukkitPotion).start();

					return bukkitPotion;
				}
				else {
					final CustomPotion notchPotion = new CustomPotion(location, potionId, notchPlayer) {
						@Override
						protected boolean hit(MovingObjectPosition movingobjectposition) {
							final org.bukkit.World world = getBukkitEntity().getWorld();
							world.playEffect(new Location(world, this.locX, this.locY, this.locZ), Effect.POTION_BREAK, potionId);
							return true;
						}
					};

					notchWorld.addEntity(notchPotion);
					return notchPotion.getBukkitEntity();
				}
			}

		case "fakeitem":
			final FakeShapeBasedEntity itemEntity = new FakeShapeBasedEntity(location, "item");
			if (data != null) {
				CommandSender themSender = them.getEntity();
				if (themSender == null)
					themSender = commandSender;

				itemEntity.runAction(themSender, "type "+data.replace("*", " "));
			}
			itemEntity.send();

			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() { public void run() {
				itemEntity.remove();
			}}, 200);

			return itemEntity;

		case "arrow":
			return world.spawnArrow(location, new Vector(0, 1, 0), 2, 0);

		case "minecart":
		case "cart":
			return world.spawn(location, Minecart.class);

		case "xp":
		case "xpball":
			return world.spawn(location, ExperienceOrb.class);

		case "fireworks":
		case "firework":
		case "fw":
			final net.minecraft.server.v1_7_R3.ItemStack fireworks;
			if (commandSender instanceof Player && ((Player) commandSender).getItemInHand().getType() == Material.FIREWORK) {
				fireworks = CraftItemStack.asNMSCopy(((Player) commandSender).getItemInHand());
			}
			else if (data == null) {
				fireworks = makeFireworks(1, 0, (int)(Math.random() * (1 << 24)), (int)(Math.random() * (1 << 24)), (int)(Math.random() * (1 << 24)));
			}
			else {
				fireworks = makeFireworks(data);
			}

			return explodeFirework(location, fireworks);

		case "fakexp":
		case "fakeball":
			final FakeEntity fakeEntity = new FakeExperienceOrb(location, 1);
			fakeEntity.send();
			fakeEntity.teleport(location);

			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() { public void run() {
				fakeEntity.remove();
			}}, 1000);
			return fakeEntity;

		case "boat":
			return world.spawn(location, Boat.class);

		case "creeper":
			final Creeper creeper = (Creeper) world.spawnEntity(location, EntityType.CREEPER);
			if (creeper == null) {
				throw new FoxBukkitCommandException("Could not spawn a creeper here. Too bright?");
			}

			if ("ELECTRIFIED".equalsIgnoreCase(data) || "CHARGED".equalsIgnoreCase(data) || "POWERED".equalsIgnoreCase(data)) {
				creeper.setPowered(true);
			}

			return creeper;

		case "slime":
			final Slime slime = (Slime) world.spawnEntity(location, EntityType.SLIME);

			if (data != null) {
				try {
					int size = Integer.parseInt(data);
					slime.setSize(size);
				}
				catch (NumberFormatException ignored) { }
			}

			return slime;

		case "wolf":
			final Wolf wolf = (Wolf) world.spawnEntity(location, EntityType.WOLF);

			if (data == null) {
				return wolf;
			}

			for (String subData : data.toUpperCase().split(",")) {
				if (subData.isEmpty())
					continue;

				switch (subData) {
				case "ANGRY":
					wolf.setAngry(true);
					break;

				case "SITTING":
				case "SIT":
					wolf.setSitting(true);
					break;

				case "TAME":
				case "TAMED":
					if (them == null)
						wolf.setOwner(ICommand.asPlayer(commandSender));
					else
						wolf.setOwner(them.getEntity());
					break;
				}
			}

			return wolf;

		case "sheep":
			final Sheep sheep = (Sheep) world.spawnEntity(location, EntityType.SHEEP);

			if (data == null)
				return sheep;

			switch (data.toLowerCase()) {
			case "camo":
			case "camouflage":
				new CamoSheep(plugin, sheep);
				break;

			case "party":
				new PartySheep(plugin, sheep);
				break;

			case "trap":
				new TrapEntity(plugin, sheep);
				break;

			case "sheared":
			case "shorn":
			case "nude":
			case "naked":
				sheep.setSheared(true);

			default:
				DyeColor dyeColor = DyeColor.WHITE;
				try {
					if ("RAINBOW".equalsIgnoreCase(data) || "RAINBOWS".equalsIgnoreCase(data) || "RANDOM".equalsIgnoreCase(data)) {
						DyeColor[] dyes = DyeColor.values();
						dyeColor = dyes[(int)Math.floor(dyes.length*Math.random())];
					}
					else {
						dyeColor = DyeColor.valueOf(data.toUpperCase());
					}
				}
				catch (Exception ignored) { }

				sheep.setColor(dyeColor);
			}

			return sheep;

		case "npc":
			final String name = data == null ? "" : data;
			return SpawnUtilsNPCDependency.makeNPC(name, location).getBukkitEntity();

		case "ocelot":
		case "cat":
			final Ocelot ocelot = (Ocelot) world.spawnEntity(location, EntityType.OCELOT);
			if (data != null) {
				Ocelot.Type oType = null;

				for (String subData : data.toUpperCase().split(",")) {
					if (subData.isEmpty())
						continue;

					if (subData.equals("SITTING") || subData.equals("SIT")) {
						ocelot.setSitting(true);
					} else {
						Ocelot.Type tmpOType = null;
						try {
							String filteredData = subData.toUpperCase().replace(' ','_');
							if(subData.endsWith("_CAT")) {
								tmpOType = Ocelot.Type.valueOf(filteredData);
							} else if(subData.equals("WILD") || subData.equals("OCELOT") || subData.equals("WILD_OCELOT")) {
								tmpOType = Ocelot.Type.WILD_OCELOT;
							} else {
								tmpOType = Ocelot.Type.valueOf(filteredData + "_CAT");
							}
						}
						catch(Exception ignored) { }

						if(tmpOType != null) {
							oType = tmpOType;
						}
					}
				}

				if(oType != null) {
					ocelot.setCatType(oType);
				}
			}

			return ocelot;

		case "snowball":
			final EntitySnowball notchSnowball;
			if (them == null) {
				notchSnowball = new EntitySnowball(notchWorld, ICommand.asNotchPlayer(commandSender));
			} else {
				notchSnowball = new EntitySnowball(notchWorld, (EntityPlayer) them.getInternalEntity());
			}

			notchWorld.addEntity(notchSnowball);
			return notchSnowball.getBukkitEntity();

		case "particle":
			final FakeEntityParticleSpawner particleSpawner = makeParticleSpawner(location, data, 3, 0, new Vector());
			particleSpawner.send();
			return particleSpawner;

		case "head":
			return world.dropItem(location, makeHeadFromData(data));

		case "fakehead":
			final FakeShapeBasedEntity head = new FakeShapeBasedEntity(location, "item");
			final ItemShape shape = (ItemShape) head.getShape();

			shape.setItemStack(makeHeadFromData(data));

			head.send();

			return head;

		default:
			try {
				final EntityType entityType = EntityType.valueOf(type.toUpperCase());
				return world.spawnEntity(location, entityType);
			}
			catch (IllegalArgumentException e) {
				throw new FoxBukkitCommandException("Creature type "+type+" not found", e);
			}
		}
	}

	private static Vector parseVector(String s) {
		final String[] parts = s.split(",");

		final double x = Double.parseDouble(parts[0]);
		final double y = Double.parseDouble(parts[1]);
		final double z = Double.parseDouble(parts[2]);

		return new Vector(x, y, z);
	}

	public static Entity explodeFirework(Location location, net.minecraft.server.v1_7_R3.ItemStack fireworks) {
		final FakeVehicle fakeEntity = new FakeVehicle(location, 76);
		fakeEntity.send();

		fakeEntity.setData(8, fireworks);

		fakeEntity.teleport(location);

		fakeEntity.sendEntityStatus((byte) 17);
		fakeEntity.remove();
		return fakeEntity;
	}

	public static net.minecraft.server.v1_7_R3.ItemStack makeFireworks(final String fireworkType) {
		final String[] parameters = fireworkType.split("/");
		final int[] colors = parseColors(parameters[0].split(","));
		final net.minecraft.server.v1_7_R3.ItemStack fireworks = makeFireworks(-127, 0, colors);
		final NBTTagCompound explosionTag = fireworks.getTag().getCompound("Fireworks").getList("Explosions", Utils.mapNBT("Explosions")).get(0);
		for (int i = 1; i < parameters.length; ++i) {
			final String[] kv = parameters[i].split("=");
			final String key = kv[0];
			final String value = kv.length > 1 ? kv[1] : "1";
			if (key.equalsIgnoreCase("Fade")) {
				final int[] fadeColors = parseColors(value.split(","));
				explosionTag.setIntArray("FadeColors", fadeColors);
			}
			else {
				explosionTag.setByte(key, Byte.parseByte(value));
			}
		}
		return fireworks;
	}

	private static int[] parseColors(final String[] colorStrings) {
		final int[] colors = new int[colorStrings.length];
		for (int i = 0; i < colorStrings.length; ++i) {
			final String colorString = colorStrings[i];
			if (colorString.charAt(0) == 'r') {
				colors[i] = (int)(Math.random() * (1 << 24));
			}
			else {
				colors[i] = Integer.parseInt(colorString, 16);
			}
		}
		return colors;
	}

	public static net.minecraft.server.v1_7_R3.ItemStack makeFireworks(int nGunpowder, int explosionType, int... explosionColors) {
		final NBTTagCompound explosionTag = new NBTTagCompound();
		explosionTag.setByte("Type", (byte) explosionType);
		explosionTag.setIntArray("Colors", explosionColors);

		final NBTTagList explosionsTag = new NBTTagList();
		explosionsTag.add(explosionTag);

		final NBTTagCompound fireworksTag = new NBTTagCompound();
		fireworksTag.setByte("Flight", (byte)nGunpowder);
		fireworksTag.set("Explosions", explosionsTag);

		final NBTTagCompound itemStackTag = new NBTTagCompound();
		itemStackTag.set("Fireworks", fireworksTag);

		final net.minecraft.server.v1_7_R3.ItemStack stack = new net.minecraft.server.v1_7_R3.ItemStack(Items.FIREWORKS);
		stack.setTag(itemStackTag);
		return stack;
	}

	public void checkMobSpawn(CommandSender commandSender, String mobName) throws PermissionDeniedException {
		if (!commandSender.hasPermission("foxbukkit.mobspawn."+mobName.toLowerCase()))
			throw new PermissionDeniedException();
	}

	public static void makeParticles(Location location, Vector scatter, double particleSpeed, int numParticles, String particleName) {
		if (!isValidParticle(particleName))
			return;

		final PacketPlayOutWorldParticles packet63WorldParticles = createParticlePacket(location, scatter, particleSpeed, numParticles, particleName);

		FoxBukkit.instance.playerHelper.sendPacketToPlayersAround(location, 200, packet63WorldParticles);
	}

	static boolean isValidParticle(String particleName) {
		try {
			if (particleName.startsWith("iconcrack_")) {
				final int itemId = Integer.parseInt(particleName.substring(particleName.indexOf("_") + 1));
				if (itemId <= 0)
					return false;

				/* TODO: check if still necessary
				if (Item.byId[itemId] == null)
					return false;
				*/
			}
			else if (particleName.startsWith("tilecrack_")) {
				final String[] parts = particleName.split("_", 3);

				final int blockId = Integer.parseInt(parts[1]);
				if (blockId <= 0)
					return false;

				final int data = Integer.parseInt(parts[2]);
				if (data < 0)
					return false;

				if (data >= 16)
					return false;

				/* TODO: check if still necessary
				if (Block.byId[blockId] == null)
					return false;
				*/
			}
		}
		catch (Exception e) {
			return false;
		}
		return true;
	}

	public static void makeParticles(Player target, Location location, Vector scatter, double particleSpeed, int numParticles, String particleName) {
		if (!isValidParticle(particleName))
			return;

		final PacketPlayOutWorldParticles packet63WorldParticles = createParticlePacket(location, scatter, particleSpeed, numParticles, particleName);

		PlayerHelper.sendPacketToPlayer(target, packet63WorldParticles);
	}

	public static FakeEntityParticleSpawner makeParticleSpawner(Location location, final String data, int defaultNumParticles, double defaultParticleSpeed, Vector defaultScatter) {
		final String[] parts = data.split(":");
		final String particleName = parts[0];
		if (!isValidParticle(particleName))
			return null;


		final int numParticles;
		if (parts.length < 2) {
			numParticles = defaultNumParticles;
		}
		else {
			numParticles = Math.min(100, Integer.parseInt(parts[1]));
		}

		final double particleSpeed;
		if (parts.length < 3) {
			particleSpeed = defaultParticleSpeed;
		}
		else {
			particleSpeed = Math.min(10, Double.parseDouble(parts[2]));
		}

		final Vector scatter;
		if (parts.length < 4) {
			scatter = defaultScatter;
		}
		else {
			scatter = Vector.getMinimum(new Vector(10, 10, 10), parseVector(parts[3]));
		}

		return new FakeEntityParticleSpawner(location, scatter, particleSpeed, numParticles, particleName);
	}

	public static PacketPlayOutWorldParticles createParticlePacket(Location location, Vector scatter, double particleSpeed, int numParticles, String particleName) {
		if (!isValidParticle(particleName))
			throw new RuntimeException("Invalid particle name");

		final PacketPlayOutWorldParticles packet63WorldParticles = new PacketPlayOutWorldParticles();
		packet63WorldParticles.a = particleName; // v1_7_R1

		packet63WorldParticles.b = (float) location.getX(); // v1_7_R1
		packet63WorldParticles.c = (float) location.getY(); // v1_7_R1
		packet63WorldParticles.d = (float) location.getZ(); // v1_7_R1

		packet63WorldParticles.e = (float) scatter.getX(); // v1_7_R1
		packet63WorldParticles.f = (float) scatter.getY(); // v1_7_R1
		packet63WorldParticles.g = (float) scatter.getZ(); // v1_7_R1

		packet63WorldParticles.h = (float) particleSpeed; // v1_7_R1

		packet63WorldParticles.i = numParticles; // v1_7_R1
		return packet63WorldParticles;
	}

	private ItemStack makeHeadFromData(final String data) {
		if (data == null)
			return makeHead(EntityType.PLAYER);

		return makeHead(data);
	}

	public ItemStack makeHead(String playerName) {
		final ItemStack toDrop = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);

		final SkullMeta meta = (SkullMeta) toDrop.getItemMeta();
		meta.setOwner(playerName);
		meta.setDisplayName(ChatColor.RESET + playerName + "'s Head");
		toDrop.setItemMeta(meta);

		return toDrop;
	}

	public ItemStack makeHead(EntityType entityType) {
		switch (entityType) {
		case PLAYER:
			return new ItemStack(Material.SKULL_ITEM, 1, (short) 3);

		case ZOMBIE:
			return new ItemStack(Material.SKULL_ITEM, 1, (short) 2);

		case CREEPER:
			return new ItemStack(Material.SKULL_ITEM, 1, (short) 4);

		case SKELETON:
			return new ItemStack(Material.SKULL_ITEM, 1, (short) 0);

		default:
			return null;
		}
	}
}
