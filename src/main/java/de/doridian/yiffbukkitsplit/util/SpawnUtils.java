package de.doridian.yiffbukkitsplit.util;

import de.doridian.yiffbukkit.main.PermissionDeniedException;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.ICommand;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import de.doridian.yiffbukkitsplit.effects.EffectProperties;
import de.doridian.yiffbukkitsplit.effects.YBEffect;
import de.doridian.yiffbukkit.spawning.fakeentity.FakeEntity;
import de.doridian.yiffbukkit.spawning.fakeentity.FakeExperienceOrb;
import de.doridian.yiffbukkit.spawning.sheep.CamoSheep;
import de.doridian.yiffbukkit.spawning.sheep.PartySheep;
import de.doridian.yiffbukkit.spawning.sheep.TrapSheep;
import net.minecraft.server.EntityFallingBlock;
import net.minecraft.server.EntityFireball;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.EntityPotion;
import net.minecraft.server.EntityTNTPrimed;
import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.MovingObjectPosition;
import net.minecraft.server.NetServerHandler;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.WorldServer;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
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
	private YiffBukkit plugin;

	public SpawnUtils(YiffBukkit plugin) {
		this.plugin = plugin;
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
			final String[] partparts = part.split(":");

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
				previous.setPassenger(entity);
			}

			previous = entity;
		}
		if (first == null) {
			throw new YiffBukkitCommandException("Unknown error occured while spawning entity.");
		}
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

			final EntityFireball notchEntity = new EntityFireball(notchWorld, playerEntity, dx, dy, dz);
			notchEntity.locX = location.getX();
			notchEntity.locY = location.getY();
			notchEntity.locZ = location.getZ();

			double d3 = 0.1D / Math.sqrt(dx * dx + dy * dy + dz * dz);

			notchEntity.dirX = dx * d3;
			notchEntity.dirY = dy * d3;
			notchEntity.dirZ = dz * d3;

			notchWorld.addEntity(notchEntity);
			return null;//notchEntity.getBukkitEntity();
		}
		else if (type.equalsIgnoreCase("TNT")) {
			EntityTNTPrimed notchEntity = new EntityTNTPrimed(notchWorld, 0, 1, 0);

			notchWorld.addEntity(notchEntity);
			final Entity entity = notchEntity.getBukkitEntity();

			entity.teleport(location);

			return entity;
		}
		else if (type.equalsIgnoreCase("SAND") || type.equalsIgnoreCase("GRAVEL")) {
			int material = Material.valueOf(type.toUpperCase()).getId();
			EntityFallingBlock notchEntity = new EntityFallingBlock(notchWorld, location.getX(), location.getY(), location.getZ(), material, 0);

			notchWorld.addEntity(notchEntity);
			return notchEntity.getBukkitEntity();
		}
		else if (type.equalsIgnoreCase("LIGHTNING") || (type.equalsIgnoreCase("POTION") && "LIGHTNING".equalsIgnoreCase(data))) {
			final EntityPlayer notchPlayer = ((CraftPlayer) commandSender).getHandle();

			net.minecraft.server.Entity notchEntity = new CustomPotion(location, 10, notchPlayer) {
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
			final EntityPlayer notchPlayer = ICommand.asCraftPlayer(commandSender).getHandle();

			if ("NINJA".equalsIgnoreCase(data)) {
				final net.minecraft.server.Entity notchEntity = new CustomPotion(location, 8, notchPlayer) {
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

					final net.minecraft.server.Entity notchEntity = new AreaCustomPotion(location, effectProperties.potionColor(), notchPlayer, effectProperties.radius()) {
						@Override
						protected void areaHit(final Entity entity) {
							try {
								YBEffect.create(data.toLowerCase(), entity).start();
							} catch (YiffBukkitCommandException e) {
								e.printStackTrace(); // TEMP!!!
							}
						}
					};

					notchWorld.addEntity(notchEntity);
					final Entity entity = notchEntity.getBukkitEntity();

					YBEffect.createTrail(data.toLowerCase(), entity).start();

					return entity;
				}
				else {
					final net.minecraft.server.Entity notchEntity = new CustomPotion(location, potionId, notchPlayer) {
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
		else if (type.equalsIgnoreCase("ARROW")) {
			return world.spawnArrow(location, new Vector(0, 1, 0), 2, 0);
		}
		else if (type.equalsIgnoreCase("MINECART") || type.equalsIgnoreCase("CART")) {
			return world.spawn(location, Minecart.class);
		}
		else if (type.equalsIgnoreCase("XP") || type.equalsIgnoreCase("XPBALL")) {
			return world.spawn(location, ExperienceOrb.class);
		}
		else if (type.equalsIgnoreCase("FAKEXP") || type.equalsIgnoreCase("FAKEBALL")) {
			final FakeEntity a = new FakeExperienceOrb(location, 1);
			a.send();
			a.teleport(location);

			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() { public void run() {
				a.remove();
			}}, 1000);
			return a;
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

	public void checkMobSpawn(CommandSender commandSender, String mobName) throws PermissionDeniedException {
		if (!commandSender.hasPermission("yiffbukkit.mobspawn."+mobName.toLowerCase()))
			throw new PermissionDeniedException();
	}

	public static HumanEntity makeNPC(String name, Location location) {
		// Get some notch-type references
		final WorldServer worldServer = ((CraftWorld)location.getWorld()).getHandle();
		final MinecraftServer minecraftServer = worldServer.server;

		// Create the new player
		final EntityPlayer eply = new EntityPlayer(minecraftServer, worldServer, name, new ItemInWorldManager(worldServer));

		// Create network manager for the player
		final NetworkManager networkManager = new NetworkManager(new NPCSocket(), eply.name, null);
		// Create NetServerHandler. This will automatically write itself to the player and networkmanager
		new NetServerHandler(minecraftServer, networkManager, eply);

		// teleport it to the target location
		eply.netServerHandler.teleport(location);
		//bukkitEntity.teleport(location);

		// Finally, put the entity into the world.
		worldServer.addEntity(eply);

		// The entity should neither show up in the world player list...
		worldServer.players.remove(eply);

		// ...nor in the server player list (i.e. /list /who and the likes)
		minecraftServer.serverConfigurationManager.players.remove(eply);

		// finally obtain a bukkit entity,
		final HumanEntity bukkitEntity = (HumanEntity) eply.getBukkitEntity();

		// and return it
		return bukkitEntity;
	}

	private abstract class AreaCustomPotion extends CustomPotion {
		private double radius;

		public AreaCustomPotion(Location location, int potionId, EntityPlayer thrower, double radius) {
			super(location, potionId, thrower);
			this.radius = radius;
		}

		protected abstract void areaHit(Entity entity) throws YiffBukkitCommandException;
		protected void directHit(Entity entity) throws YiffBukkitCommandException {
			areaHit(entity);
		}

		@Override
		protected boolean hit(MovingObjectPosition movingobjectposition) throws YiffBukkitCommandException {
			final Entity thisBukkitEntity = getBukkitEntity();
			final World world = thisBukkitEntity.getWorld();
			world.playEffect(new Location(world, this.locX, this.locY, this.locZ), Effect.POTION_BREAK, potionId);

			Entity directHitEntity = null;
			if (movingobjectposition.entity != null) {
				directHitEntity = movingobjectposition.entity.getBukkitEntity();
				directHit(directHitEntity);
			}

			final Location thisLocation = thisBukkitEntity.getLocation();

			for (Entity entity: thisBukkitEntity.getNearbyEntities(radius, radius, radius)) {
				if (entity.getLocation().distanceSquared(thisLocation) > radius*radius)
					continue;

				if (entity.equals(thrower))
					continue;

				if (entity.equals(directHitEntity))
					continue;

				areaHit(entity);
			}

			return true;
		}
	}

	private abstract class CustomPotion extends EntityPotion {
		protected final int potionId;
		protected final EntityPlayer thrower;

		private CustomPotion(Location location, int potionId, EntityPlayer thrower) {
			super(((CraftWorld) location.getWorld()).getHandle(), location.getX(), location.getY(), location.getZ(), potionId);
			this.potionId = potionId;
			this.thrower = thrower;
		}

		@Override
		protected void a(MovingObjectPosition movingobjectposition) {
			if (movingobjectposition.entity == thrower)
				return;

			try {
				if (hit(movingobjectposition))
					die();
			}
			catch (YiffBukkitCommandException e) {
				plugin.playerHelper.sendDirectedMessage((CommandSender) thrower.getBukkitEntity(), e.getMessage(), e.getColor());
				die();
			}
			catch (Throwable e) {
				e.printStackTrace();
				die();
			}
		}

		protected abstract boolean hit(MovingObjectPosition movingobjectposition) throws YiffBukkitCommandException;
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
}
