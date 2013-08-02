package de.doridian.yiffbukkit.spawning;

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
import de.doridian.yiffbukkit.spawning.sheep.TrapSheep;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import net.minecraft.server.v1_6_R2.EntityFallingBlock;
import net.minecraft.server.v1_6_R2.EntityLargeFireball;
import net.minecraft.server.v1_6_R2.EntityPlayer;
import net.minecraft.server.v1_6_R2.EntitySnowball;
import net.minecraft.server.v1_6_R2.EntityTNTPrimed;
import net.minecraft.server.v1_6_R2.Item;
import net.minecraft.server.v1_6_R2.ItemStack;
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
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_6_R2.CraftServer;
import org.bukkit.craftbukkit.v1_6_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_6_R2.inventory.CraftItemStack;
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
import org.bukkit.util.Vector;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SpawnUtils {
	private final YiffBukkit plugin;
	private final Entity noErrorPlz;

	public SpawnUtils(YiffBukkit plugin) {
		this.plugin = plugin;
		this.noErrorPlz = new FakeEntityParticleSpawner(new Location(null, 0, 0, 0), new Vector(), 0, 0, "");
	}

	public Entity buildMob(final String[] types, final CommandSender commandSender, Player them, Location location) throws YiffBukkitCommandException {
		boolean hasThis = false;
		for (String part : types) {
			if ("THIS".equalsIgnoreCase(part)) {
				hasThis = true;
				break;
			}
		}

		Entity thisEnt = null;
		if (hasThis) {
			Vector eyeVector = location.getDirection();
			Vector eyeOrigin = location.toVector();

			for (Entity currentEntity : location.getWorld().getEntities()) {
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

				Vector pos = eyeLocation.toVector();
				pos.add(new Vector(0, 0.6, 0));

				pos.subtract(eyeOrigin);

				if (pos.lengthSquared() > 9)
					continue;

				double dot = pos.clone().normalize().dot(eyeVector);

				if (dot < 0.8)
					continue;


				if (currentEntity.equals(commandSender))
					continue;

				thisEnt = currentEntity;
				break;
			}
			if (thisEnt == null) {
				throw new YiffBukkitCommandException("You must face a creature/boat/minecart");
			}
		}

		Entity previous = null;
		Entity first = null;
		for (String part : types) {
			final String[] partparts = part.split(":", 2);

			final String type = partparts[0];
			final String data = partparts.length >= 2 ? partparts[1] : null;

			checkMobSpawn(commandSender, type);

			final Entity entity;
			if (type.equalsIgnoreCase("THIS")) {
				entity = thisEnt;
			}
			else {
				entity = spawnSingleMob(commandSender, them, location, type, data);
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
			Player them, Location location, final String type,
			final String data)
			throws YiffBukkitCommandException {

		final World world = location.getWorld();
		final WorldServer notchWorld = ((CraftWorld)world).getHandle();

		if (type.equalsIgnoreCase("ME")) {
			return ICommand.asPlayer(commandSender);
		}
		else if (type.equalsIgnoreCase("THEM")) {
			return them;
		}
		else if (type.equalsIgnoreCase("FIREBALL")) {
			final EntityPlayer playerEntity;
			if (them instanceof CraftPlayer)
				playerEntity = ((CraftPlayer)them).getHandle();
			else if (commandSender instanceof CraftPlayer)
				playerEntity = ((CraftPlayer)commandSender).getHandle();
			else
				playerEntity = null;

			final Vector dir = playerEntity.getBukkitEntity().getLocation().getDirection();
			double dx = dir.getX();
			double dy = dir.getY();
			double dz = dir.getZ();

			final EntityLargeFireball notchEntity = new EntityLargeFireball(notchWorld, playerEntity, dx, dy, dz);
			notchEntity.locX = location.getX();
			notchEntity.locY = location.getY();
			notchEntity.locZ = location.getZ();

			double d3 = 0.1D / Math.sqrt(dx * dx + dy * dy + dz * dz);

			notchEntity.dirX = dx * d3;
			notchEntity.dirY = dy * d3;
			notchEntity.dirZ = dz * d3;

			notchWorld.addEntity(notchEntity);
			return noErrorPlz;//notchEntity.getBukkitEntity();
		}
		else if (type.equalsIgnoreCase("TNT")) {
			final EntityPlayer playerEntity;
			if (commandSender instanceof CraftPlayer)
				playerEntity = ((CraftPlayer)commandSender).getHandle();
			else
				playerEntity = null;

			EntityTNTPrimed notchEntity = new EntityTNTPrimed(notchWorld, 0, 1, 0, playerEntity);

			notchWorld.addEntity(notchEntity);
			final Entity entity = notchEntity.getBukkitEntity();

			entity.teleport(location);

			return entity;
		}
		else if (type.equalsIgnoreCase("SAND") || type.equalsIgnoreCase("GRAVEL") || type.equalsIgnoreCase("ANVIL") || type.equalsIgnoreCase("BLOCK")) {
			int typeId;
			final int dataValue;
			if (type.equalsIgnoreCase("BLOCK")) {
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

			EntityFallingBlock notchEntity = new EntityFallingBlock(notchWorld, location.getX(), location.getY(), location.getZ(), typeId, dataValue);

			// This disables the first tick code, which takes care of removing the original block etc.
			notchEntity.c = 1; // v1_6_R2

			// Do not drop an item if placing a block fails
			notchEntity.dropItem = false;

			notchWorld.addEntity(notchEntity);
			return notchEntity.getBukkitEntity();
		}
		else if (type.equalsIgnoreCase("LIGHTNING") || (type.equalsIgnoreCase("POTION") && "LIGHTNING".equalsIgnoreCase(data))) {
			final EntityPlayer notchPlayer = ((CraftPlayer) commandSender).getHandle();

			net.minecraft.server.v1_6_R2.Entity notchEntity = new CustomPotion(location, 10, notchPlayer) {
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
		else if (type.equalsIgnoreCase("POTION")) {
			final EntityPlayer notchPlayer = ICommand.asNotchPlayer(commandSender, null);

			if ("NINJA".equalsIgnoreCase(data)) {
				final net.minecraft.server.v1_6_R2.Entity notchEntity = new CustomPotion(location, 8, notchPlayer) {
					@Override
					protected boolean hit(MovingObjectPosition movingobjectposition) throws YiffBukkitCommandException {
						final Entity thisBukkitEntity = getBukkitEntity();
						final World world = thisBukkitEntity.getWorld();
						world.playEffect(new Location(world, this.locX, this.locY, this.locZ), Effect.POTION_BREAK, potionId);

						plugin.playerHelper.vanish(ICommand.asPlayer(commandSender));

						return true;
					}
				};

				notchWorld.addEntity(notchEntity);
				return notchEntity.getBukkitEntity();
			}
			else if (data.toUpperCase().startsWith("METEOR")) {
				String[] parts = data.split(":");
				if (!"METEOR".equalsIgnoreCase(parts[0]))
					return null;

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

				final net.minecraft.server.v1_6_R2.Entity notchEntity = new Meteor(location, notchPlayer, radius, speed);

				notchWorld.addEntity(notchEntity);
				return notchEntity.getBukkitEntity();
			}
			else {
				int potionId = -1;
				try {
					potionId = Integer.parseInt(data);
				}
				catch (NumberFormatException e) { }

				if (potionId == -1) {
					final EffectProperties effectProperties = YBEffect.getEffectProperties(data.toLowerCase());
					if (effectProperties == null)
						throw new YiffBukkitCommandException("Effect '"+data+"' does not exist");

					final net.minecraft.server.v1_6_R2.Entity notchEntity = new AreaCustomPotion(location, effectProperties.potionColor(), notchPlayer, effectProperties.radius()) {
						@Override
						protected void areaHit(final Entity entity) {
							try {
								YBEffect.create(data.toLowerCase(), entity).start();
							} catch (YiffBukkitCommandException e) {
							}
						}
					};

					notchWorld.addEntity(notchEntity);
					final Entity entity = notchEntity.getBukkitEntity();

					YBEffect.createTrail(data.toLowerCase(), entity).start();

					return entity;
				}
				else {
					final net.minecraft.server.v1_6_R2.Entity notchEntity = new CustomPotion(location, potionId, notchPlayer) {
						@Override
						protected boolean hit(MovingObjectPosition movingobjectposition) {
							org.bukkit.World world = getBukkitEntity().getWorld();
							world.playEffect(new Location(world, this.locX, this.locY, this.locZ), Effect.POTION_BREAK, potionId);
							return true;
						}
					};

					notchWorld.addEntity(notchEntity);
					return notchEntity.getBukkitEntity();
				}
			}
		}
		else if (type.equalsIgnoreCase("FAKEITEM")) {
			final FakeShapeBasedEntity entity = new FakeShapeBasedEntity(location, "item");
			if (data != null) {
				entity.runAction(them, "type "+data.replace("*", " "));
			}
			entity.send();

			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() { public void run() {
				entity.remove();
			}}, 200);

			return entity;
		}
		else if (type.equalsIgnoreCase("ARROW")) {
			return world.spawnArrow(location, new Vector(0, 1, 0), 2, 0);
		}
		else if (type.equalsIgnoreCase("MINECART") || type.equalsIgnoreCase("CART")) {
			return world.spawn(location, Minecart.class);
		}
		else if (type.equalsIgnoreCase("XP") || type.equalsIgnoreCase("XPBALL")) {
			return world.spawn(location, ExperienceOrb.class);
		}
		else if (type.equalsIgnoreCase("fireworks") || type.equalsIgnoreCase("firework") || type.equalsIgnoreCase("fw")) {
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
		}
		else if (type.equalsIgnoreCase("FAKEXP") || type.equalsIgnoreCase("FAKEBALL")) {
			final FakeEntity fakeEntity = new FakeExperienceOrb(location, 1);
			fakeEntity.send();
			fakeEntity.teleport(location);

			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() { public void run() {
				fakeEntity.remove();
			}}, 1000);
			return fakeEntity;
		}
		else if (type.equalsIgnoreCase("BOAT")) {
			return world.spawn(location, Boat.class);
		}
		else if (type.equalsIgnoreCase("CREEPER")) {
			final Entity entity = world.spawnEntity(location, EntityType.CREEPER);
			if (entity == null) {
				throw new YiffBukkitCommandException("Could not spawn a creeper here. Too bright?");
			}
			final Creeper creeper = (Creeper)entity;

			if ("ELECTRIFIED".equalsIgnoreCase(data) || "CHARGED".equalsIgnoreCase(data) || "POWERED".equalsIgnoreCase(data)) {
				creeper.setPowered(true);
			}
			return entity;
		}
		else if (type.equalsIgnoreCase("SLIME")) {
			final Slime slime = (Slime) world.spawnEntity(location, EntityType.SLIME);

			if (data != null) {
				try {
					int size = Integer.parseInt(data);
					slime.setSize(size);
				}
				catch (NumberFormatException e) { }
			}

			return slime;
		}
		else if (type.equalsIgnoreCase("WOLF")) {
			final Wolf wolf = (Wolf) world.spawnEntity(location, EntityType.WOLF);

			if (data != null) { 
				for (String subData : data.toUpperCase().split(",")) {
					if (subData.isEmpty())
						continue;

					if (subData.equals("ANGRY")) {
						wolf.setAngry(true);
					}
					else if (subData.equals("SITTING") || subData.equals("SIT")) {
						wolf.setSitting(true);
					}
					else if (subData.equals("TAME") || subData.equals("TAMED")) {
						if (them == null)
							wolf.setOwner(ICommand.asPlayer(commandSender));
						else
							wolf.setOwner(them);
					}
				}
			}

			return wolf;
		}
		else if (type.equalsIgnoreCase("SHEEP")) {
			final Sheep sheep = (Sheep) world.spawnEntity(location, EntityType.SHEEP);

			if ("CAMO".equalsIgnoreCase(data) || "CAMOUFLAGE".equalsIgnoreCase(data)) {
				new CamoSheep(plugin, sheep);
			}
			else if ("PARTY".equalsIgnoreCase(data)) {
				new PartySheep(plugin, sheep);
			}
			else if ("TRAP".equalsIgnoreCase(data)) {
				new TrapSheep(plugin, sheep);
			}
			else if ("SHEARED".equalsIgnoreCase(data) || "SHORN".equalsIgnoreCase(data) || "NUDE".equalsIgnoreCase(data) || "NAKED".equalsIgnoreCase(data)) {
				sheep.setSheared(true);
			}
			else {
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
				catch (Exception e) { }

				sheep.setColor(dyeColor);
			}

			return sheep;
		}
		else if (type.equalsIgnoreCase("NPC")) {
			final String name = data == null ? "" : data;
			return makeNPC(name, location);
		}
		else if(type.equalsIgnoreCase("OCELOT") || type.equalsIgnoreCase("CAT")) {
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
						} catch(Exception e) { }

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
		}
		else if(type.equalsIgnoreCase("SNOWBALL")) {
			final EntitySnowball notchEntity;
			if (them == null) {
				notchEntity = new EntitySnowball(notchWorld, ICommand.asNotchPlayer(commandSender));
			} else {
				notchEntity = new EntitySnowball(notchWorld, ICommand.asNotchPlayer(them));
			}

			notchWorld.addEntity(notchEntity);
			return notchEntity.getBukkitEntity();
		}
		else {
			try {
				EntityType creatureType = EntityType.valueOf(type.toUpperCase());
				return world.spawnEntity(location, creatureType);
			}
			catch (IllegalArgumentException e) {
				throw new YiffBukkitCommandException("Creature type "+type+" not found", e);
			}
		}
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

	public static ItemStack makeFireworks(int nGunpowder, int explosionType, int... explosionColors) {
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

		// finally obtain a bukkit entity,
		final HumanEntity bukkitEntity = (HumanEntity) eply.getBukkitEntity();

		// and return it
		return bukkitEntity;
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
		final Packet63WorldParticles packet63WorldParticles = createParticlePacket(location, scatter, particleSpeed, numParticles, particleName);

		YiffBukkit.instance.playerHelper.sendPacketToPlayersAround(location, 200, packet63WorldParticles);
	}

	public static void makeParticles(Player target, Location location, Vector scatter, double particleSpeed, int numParticles, String particleName) {
		final Packet63WorldParticles packet63WorldParticles = createParticlePacket(location, scatter, particleSpeed, numParticles, particleName);

		PlayerHelper.sendPacketToPlayer(target, packet63WorldParticles);
	}

	public static Packet63WorldParticles createParticlePacket(Location location, Vector scatter, double particleSpeed, int numParticles, String particleName) {
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
}
