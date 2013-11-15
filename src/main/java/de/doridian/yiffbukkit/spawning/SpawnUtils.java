package de.doridian.yiffbukkit.spawning;

import de.doridian.yiffbukkit.advanced.listeners.YiffBukkitHeadChopOffListener;
import de.doridian.yiffbukkit.main.PermissionDeniedException;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkit.spawning.effects.system.EffectProperties;
import de.doridian.yiffbukkit.spawning.effects.system.YBEffect;
import de.doridian.yiffbukkit.spawning.fakeentity.FakeEntity;
import de.doridian.yiffbukkit.spawning.fakeentity.FakeEntityParticleSpawner;
import de.doridian.yiffbukkit.spawning.fakeentity.FakeExperienceOrb;
import de.doridian.yiffbukkit.spawning.fakeentity.FakeShapeBasedEntity;
import de.doridian.yiffbukkit.spawning.fakeentity.FakeVehicle;
import de.doridian.yiffbukkit.spawning.potions.AreaCustomPotion;
import de.doridian.yiffbukkit.spawning.potions.CustomPotion;
import de.doridian.yiffbukkit.spawning.sheep.CamoSheep;
import de.doridian.yiffbukkit.spawning.sheep.PartySheep;
import de.doridian.yiffbukkit.spawning.sheep.TrapEntity;
import de.doridian.yiffbukkit.transmute.ItemShape;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import net.minecraft.server.v1_6_R2.Block;
import net.minecraft.server.v1_6_R2.EntityFallingBlock;
import net.minecraft.server.v1_6_R2.EntityLargeFireball;
import net.minecraft.server.v1_6_R2.EntityPlayer;
import net.minecraft.server.v1_6_R2.EntitySnowball;
import net.minecraft.server.v1_6_R2.EntityTNTPrimed;
import net.minecraft.server.v1_6_R2.Item;
import net.minecraft.server.v1_6_R2.MinecraftServer;
import net.minecraft.server.v1_6_R2.MovingObjectPosition;
import net.minecraft.server.v1_6_R2.NBTTagCompound;
import net.minecraft.server.v1_6_R2.NBTTagList;
import net.minecraft.server.v1_6_R2.NetworkManager;
import net.minecraft.server.v1_6_R2.Packet63WorldParticles;
import net.minecraft.server.v1_6_R2.PlayerConnection;
import net.minecraft.server.v1_6_R2.PlayerInteractManager;
import net.minecraft.server.v1_6_R2.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_6_R2.CraftServer;
import org.bukkit.craftbukkit.v1_6_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_6_R2.inventory.CraftItemStack;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.HumanEntity;
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class SpawnUtils {
	private final YiffBukkit plugin;
	private final Entity noErrorPlz;

	public SpawnUtils(YiffBukkit plugin) {
		this.plugin = plugin;
		this.noErrorPlz = new FakeEntityParticleSpawner(new Location(null, 0, 0, 0), new Vector(), 0, 0, "");
	}

	public static void logSpawn(String playerName, Location location, int amount, String typeName) {
		final double x = location.getX();
		final double y = location.getY();
		final double z = location.getZ();
		System.out.println(String.format("%s spawned %d %s at (%s,%.0f,%.0f,%.0f)", playerName, amount, typeName, location.getWorld().getName(), x, y, z));
	}

	public Entity buildMob(final String[] types, final CommandSender commandSender, Player them, final Location location) throws YiffBukkitCommandException {
		final Map<String, Spawnable<? extends Entity>> fixedSpawnables = new HashMap<>();

		final AbstractSpawnable<Entity> thisSpawnable = new AbstractSpawnable<Entity>() {
			@Override
			protected void spawn() throws YiffBukkitCommandException {
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

				throw new YiffBukkitCommandException("You must face a creature/boat/minecart");
			}
		};
		fixedSpawnables.put("this", thisSpawnable);
		fixedSpawnables.put("thisvehicle", new AbstractSpawnable<Entity>() {
			@Override
			protected void spawn() throws YiffBukkitCommandException {
				entity = thisSpawnable.getEntity().getVehicle();
			}
		});
		fixedSpawnables.put("thisvehicle", new AbstractSpawnable<Entity>() {
			@Override
			protected void spawn() throws YiffBukkitCommandException {
				entity = thisSpawnable.getEntity().getPassenger();
			}
		});

		fixedSpawnables.put("me", new AbstractSpawnable<Player>() {
			@Override
			protected void spawn() throws YiffBukkitCommandException {
				entity = ICommand.asPlayer(commandSender);
			}
		});
		fixedSpawnables.put("mevehicle", new AbstractSpawnable<Entity>() {
			@Override
			protected void spawn() throws YiffBukkitCommandException {
				entity = ICommand.asPlayer(commandSender).getVehicle();
			}
		});
		fixedSpawnables.put("mepassenger", new AbstractSpawnable<Entity>() {
			@Override
			protected void spawn() throws YiffBukkitCommandException {
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
				if (YBEffect.effectExists(attribute)) {
					final YBEffect effect = YBEffect.create(attribute, entity);
					effect.forceStart();
					continue;
				}

				switch (attribute) {
				case "leash":
					((LivingEntity) entity).setLeashHolder(them);
					break;

				case "fire":
				case "flame":
					entity.setFireTicks(entity.getFireTicks() + Math.max(20, entity.getMaxFireTicks()));
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
					YiffBukkitHeadChopOffListener.instance.addChoppedEntity(entity.getEntityId());
					break;

				case "trap":
					new TrapEntity(plugin, entity);
					break;

				case "arrows":
				case "pincushion":
					if (entity instanceof CraftLivingEntity) {
						((CraftLivingEntity) entity).getHandle().m(127);
					}
					break;
				}
			}

			if (entity == null)
				throw new YiffBukkitCommandException("Failed to spawn "+type);

			if (previous == null) {
				first = entity;
			}
			else {
				if (previous instanceof Pig)
					((Pig)previous).setSaddle(true);

				entity.teleport(location);
				if (previous == entity)
					throw new YiffBukkitCommandException("Cannot attach entities to themselves for now. Sorry.");

				previous.setPassenger(entity);
			}

			if (entity == noErrorPlz)
				continue;

			previous = entity;
		}

		if (first == null)
			throw new YiffBukkitCommandException("Unknown error occured while spawning entity.");

		return first;
	}

	private Entity spawnSingleMob(final CommandSender commandSender,
			Map<String, ? extends Spawnable<? extends Entity>> fixedSpawnables,
			Location location, String type,
			final String data)
			throws YiffBukkitCommandException {
		type = type.toLowerCase();
		final World world = location.getWorld();
		final WorldServer notchWorld = ((CraftWorld)world).getHandle();

		if (type.equals("lightning") || (type.equals("potion") && "LIGHTNING".equalsIgnoreCase(data))) {
			final EntityPlayer notchPlayer = ((CraftPlayer) commandSender).getHandle();

			final net.minecraft.server.v1_6_R2.Entity notchEntity = new CustomPotion(location, 10, notchPlayer) {
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

			final EntityFallingBlock notchFallingBlock = new EntityFallingBlock(notchWorld, location.getX(), location.getY(), location.getZ(), typeId, dataValue);

			// This disables the first tick code, which takes care of removing the original block etc.
			notchFallingBlock.c = 1; // v1_6_R2

			// Do not drop an item if placing a block fails
			notchFallingBlock.dropItem = false;

			notchWorld.addEntity(notchFallingBlock);
			return notchFallingBlock.getBukkitEntity();

		case "potion":
			final EntityPlayer notchPlayer = ICommand.asNotchPlayer(commandSender, null);

			final String[] parts = data.split(":");

			switch (parts[0].toLowerCase()) {
			case "ninja": {
				final CustomPotion notchPotion = new CustomPotion(location, 8, notchPlayer) {
					@Override
					protected boolean hit(MovingObjectPosition movingobjectposition) throws YiffBukkitCommandException {
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
					final EffectProperties effectProperties = YBEffect.getEffectProperties(data.toLowerCase());
					if (effectProperties == null)
						throw new YiffBukkitCommandException("Effect '"+data+"' does not exist");

					final CustomPotion notchPotion = new AreaCustomPotion(location, effectProperties.potionColor(), notchPlayer, effectProperties.radius()) {
						@Override
						protected void areaHit(final Entity entity) {
							try {
								YBEffect.create(data.toLowerCase(), entity).start();
							} catch (YiffBukkitCommandException ignored) {
							}
						}
					};

					notchWorld.addEntity(notchPotion);
					final Entity bukkitPotion = notchPotion.getBukkitEntity();

					YBEffect.createTrail(data.toLowerCase(), bukkitPotion).start();

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
			final net.minecraft.server.v1_6_R2.ItemStack fireworks;
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
				throw new YiffBukkitCommandException("Could not spawn a creeper here. Too bright?");
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
			return makeNPC(name, location);

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
				throw new YiffBukkitCommandException("Creature type "+type+" not found", e);
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

	public static Entity explodeFirework(Location location, net.minecraft.server.v1_6_R2.ItemStack fireworks) {
		final FakeVehicle fakeEntity = new FakeVehicle(location, 76);
		fakeEntity.send();

		fakeEntity.setData(8, fireworks);

		fakeEntity.teleport(location);

		fakeEntity.sendEntityStatus((byte) 17);
		fakeEntity.remove();
		return fakeEntity;
	}

	public static net.minecraft.server.v1_6_R2.ItemStack makeFireworks(final String fireworkType) {
		final String[] parameters = fireworkType.split("/");
		final int[] colors = parseColors(parameters[0].split(","));
		final net.minecraft.server.v1_6_R2.ItemStack fireworks = makeFireworks(-127, 0, colors);
		final NBTTagCompound explosionTag = (NBTTagCompound) fireworks.getTag().getCompound("Fireworks").getList("Explosions").get(0);
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

	public static net.minecraft.server.v1_6_R2.ItemStack makeFireworks(int nGunpowder, int explosionType, int... explosionColors) {
		final NBTTagCompound explosionTag = new NBTTagCompound("Explosion");
		explosionTag.setByte("Type", (byte) explosionType);
		explosionTag.setIntArray("Colors", explosionColors);

		final NBTTagList explosionsTag = new NBTTagList("Explosions");
		explosionsTag.add(explosionTag);

		final NBTTagCompound fireworksTag = new NBTTagCompound("Fireworks");
		fireworksTag.setByte("Flight", (byte)nGunpowder);
		fireworksTag.set("Explosions", explosionsTag);

		final NBTTagCompound itemStackTag = new NBTTagCompound();
		itemStackTag.set("Fireworks", fireworksTag);

		final net.minecraft.server.v1_6_R2.ItemStack stack = new net.minecraft.server.v1_6_R2.ItemStack(Item.FIREWORKS);
		stack.setTag(itemStackTag);
		return stack;
	}

	public void checkMobSpawn(CommandSender commandSender, String mobName) throws PermissionDeniedException {
		if (!commandSender.hasPermission("yiffbukkit.mobspawn."+mobName.toLowerCase()))
			throw new PermissionDeniedException();
	}

	public static HumanEntity makeNPC(String name, Location location) {
		// Get some notch-type references
		final WorldServer worldServer = ((CraftWorld)location.getWorld()).getHandle();
		final MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getHandle().getServer();

		// Create the new player
		final EntityPlayer eply = new EntityPlayer(minecraftServer, worldServer, name, new PlayerInteractManager(worldServer));

		// Create network manager for the player
		final NetworkManager networkManager;
		try {
			networkManager = new NetworkManager(null, new NPCSocket(), eply.getName(), null, null);
		} catch(IOException e) { return null; }

		// Create NetServerHandler. This will automatically write itself to the player and networkmanager
		new PlayerConnection(minecraftServer, networkManager, eply);

		// teleport it to the target location
		eply.playerConnection.teleport(location);
		//bukkitEntity.teleport(location);

		// Finally, put the entity into the world.
		worldServer.addEntity(eply);

		// The entity should neither show up in the world player list...
		worldServer.players.remove(eply);

		// ...nor in the server player list (i.e. /list /who and the likes)
		minecraftServer.server.getHandle().players.remove(eply);

		// finally obtain a bukkit entity and return it
		return eply.getBukkitEntity();
	}

	private static class NPCSocket extends Socket {
		final OutputStream os = new OutputStream() {
			public void write(int b) {}
			public void write(byte[] b) { }
			public void write(byte[] b, int off, int len) { }
		};
		final InputStream is = new ByteArrayInputStream(new byte[0]);

		@Override
		public OutputStream getOutputStream() throws IOException {
			return os;
		}

		@Override
		public InputStream getInputStream() throws IOException {
			return is;
		}
	}

	public static void makeParticles(Location location, Vector scatter, double particleSpeed, int numParticles, String particleName) {
		if (!isValidParticle(particleName))
			return;

		final Packet63WorldParticles packet63WorldParticles = createParticlePacket(location, scatter, particleSpeed, numParticles, particleName);

		YiffBukkit.instance.playerHelper.sendPacketToPlayersAround(location, 200, packet63WorldParticles);
	}

	static boolean isValidParticle(String particleName) {
		try {
			if (particleName.startsWith("iconcrack_")) {
				final int itemId = Integer.parseInt(particleName.substring(particleName.indexOf("_") + 1));
				if (itemId <= 0)
					return false;

				if (Item.byId[itemId] == null)
					return false;
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

				if (Block.byId[blockId] == null)
					return false;
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

		final Packet63WorldParticles packet63WorldParticles = createParticlePacket(location, scatter, particleSpeed, numParticles, particleName);

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

	public static Packet63WorldParticles createParticlePacket(Location location, Vector scatter, double particleSpeed, int numParticles, String particleName) {
		if (!isValidParticle(particleName))
			throw new RuntimeException("Invalid particle name");

		final Packet63WorldParticles packet63WorldParticles = new Packet63WorldParticles();
		Utils.setPrivateValue(Packet63WorldParticles.class, packet63WorldParticles, "a", particleName); // v1_6_R2

		Utils.setPrivateValue(Packet63WorldParticles.class, packet63WorldParticles, "b", (float) location.getX()); // v1_6_R2
		Utils.setPrivateValue(Packet63WorldParticles.class, packet63WorldParticles, "c", (float) location.getY()); // v1_6_R2
		Utils.setPrivateValue(Packet63WorldParticles.class, packet63WorldParticles, "d", (float) location.getZ()); // v1_6_R2

		Utils.setPrivateValue(Packet63WorldParticles.class, packet63WorldParticles, "e", (float) scatter.getX()); // v1_6_R2
		Utils.setPrivateValue(Packet63WorldParticles.class, packet63WorldParticles, "f", (float) scatter.getY()); // v1_6_R2
		Utils.setPrivateValue(Packet63WorldParticles.class, packet63WorldParticles, "g", (float) scatter.getZ()); // v1_6_R2

		Utils.setPrivateValue(Packet63WorldParticles.class, packet63WorldParticles, "h", (float) particleSpeed); // v1_6_R2

		Utils.setPrivateValue(Packet63WorldParticles.class, packet63WorldParticles, "i", numParticles); // v1_6_R2
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
