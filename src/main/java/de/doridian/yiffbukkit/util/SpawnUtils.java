package de.doridian.yiffbukkit.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
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
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Wolf;
import org.bukkit.util.Vector;

import de.doridian.yiffbukkit.PermissionDeniedException;
import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand;
import de.doridian.yiffbukkit.fakeentity.FakeEntity;
import de.doridian.yiffbukkit.fakeentity.FakeExperienceOrb;
import de.doridian.yiffbukkit.sheep.CamoSheep;
import de.doridian.yiffbukkit.sheep.PartySheep;
import de.doridian.yiffbukkit.sheep.TrapSheep;

public class SpawnUtils {
	private YiffBukkit plugin;
	public SpawnUtils(YiffBukkit iface) {
		plugin = iface;
	}

	public Entity buildMob(final String[] types, final CommandSender commandSender, Player them, Location location) throws YiffBukkitCommandException {
		boolean hasThis = false;
		for (String part : types) {
			if ("THIS".equalsIgnoreCase(part)) {
				hasThis = true;
				break;
			}
		}

		final World world = location.getWorld();
		final WorldServer notchWorld = ((CraftWorld)world).getHandle();

		Entity thisEnt = null;
		if (hasThis) {
			Vector eyeVector = location.getDirection().clone();
			Vector eyeOrigin = location.toVector().clone();

			for (Entity currentEntity : world.getEntities()) {
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
			String[] partparts = part.split(":");

			String type = partparts[0];
			String data = partparts.length >= 2 ? partparts[1] : null;

			checkMobSpawn(commandSender, type);

			Entity entity;
			if (type.equalsIgnoreCase("ME")) {
				entity = ICommand.asPlayer(commandSender);
			}
			else if (type.equalsIgnoreCase("THEM")) {
				entity = them;
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

				entity = null;//notchEntity.getBukkitEntity();
			}
			else if (type.equalsIgnoreCase("TNT")) {
				EntityTNTPrimed notchEntity = new EntityTNTPrimed(notchWorld, 0, 1, 0);
				notchWorld.addEntity(notchEntity);

				entity = notchEntity.getBukkitEntity();
				entity.teleport(location);
			}
			else if (type.equalsIgnoreCase("SAND") || type.equalsIgnoreCase("GRAVEL")) {
				int material = Material.valueOf(type.toUpperCase()).getId();
				EntityFallingBlock notchEntity = new EntityFallingBlock(notchWorld, location.getX(), location.getY(), location.getZ(), material, 0);
				notchWorld.addEntity(notchEntity);

				entity = notchEntity.getBukkitEntity();
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

				entity = notchEntity.getBukkitEntity();
			}
			else if (type.equalsIgnoreCase("POTION")) {
				final EntityPlayer notchPlayer = ICommand.asCraftPlayer(commandSender).getHandle();

				final net.minecraft.server.Entity notchEntity;
				if ("RAGE".equalsIgnoreCase(data)) {
					notchEntity = new CustomPotion(location, 12, notchPlayer) {
						@Override
						protected boolean hit(MovingObjectPosition movingobjectposition) {
							final Entity thisBukkitEntity = getBukkitEntity();
							final World world = thisBukkitEntity.getWorld();
							world.playEffect(new Location(world, this.locX, this.locY, this.locZ), Effect.POTION_BREAK, potionId);

							if (movingobjectposition.entity != null) {
								final Entity hitBukkitEntity = movingobjectposition.entity.getBukkitEntity();
								if (hitBukkitEntity instanceof LivingEntity) {
									plugin.playerHelper.rage((LivingEntity) hitBukkitEntity, 100);
								}
							}

							final Location thisLocation = thisBukkitEntity.getLocation();
							for (Entity entity: thisBukkitEntity.getNearbyEntities(3, 3, 3)) {
								if (!(entity instanceof LivingEntity))
									continue;

								if (entity.getLocation().distanceSquared(thisLocation) > 9)
									continue;

								if (entity.equals(thrower))
									continue;

								plugin.playerHelper.rage((LivingEntity) entity, 75);
							}
							return true;
						}
					};
				}
				else if ("NINJA".equalsIgnoreCase(data)) {
					notchEntity = new CustomPotion(location, 8, notchPlayer) {
						@Override
						protected boolean hit(MovingObjectPosition movingobjectposition) throws YiffBukkitCommandException {
							final Entity thisBukkitEntity = getBukkitEntity();
							final World world = thisBukkitEntity.getWorld();
							world.playEffect(new Location(world, this.locX, this.locY, this.locZ), Effect.POTION_BREAK, potionId);

							plugin.vanish.vanish(ICommand.asPlayer(commandSender));

							return true;
						}
					};
				}
				else {
					final int potionId = Integer.parseInt(data);
					notchEntity = new CustomPotion(location, potionId, notchPlayer) {
						@Override
						protected boolean hit(MovingObjectPosition movingobjectposition) {
							org.bukkit.World world = getBukkitEntity().getWorld();
							world.playEffect(new Location(world, this.locX, this.locY, this.locZ), Effect.POTION_BREAK, potionId);
							return true;
						}
					};
				}
				notchWorld.addEntity(notchEntity);

				entity = notchEntity.getBukkitEntity();
			}
			else if (type.equalsIgnoreCase("ARROW")) {
				entity = world.spawnArrow(location, new Vector(0, 1, 0), 2, 0);
			}
			else if (type.equalsIgnoreCase("MINECART") || type.equalsIgnoreCase("CART")) {
				entity = world.spawn(location, Minecart.class);
			}
			else if (type.equalsIgnoreCase("XP") || type.equalsIgnoreCase("XPBALL")) {
				entity = world.spawn(location, ExperienceOrb.class);
			}
			else if (type.equalsIgnoreCase("FAKEXP") || type.equalsIgnoreCase("FAKEBALL")) {
				final FakeEntity a = new FakeExperienceOrb(location, 1);
				a.send();
				a.teleport(location);

				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() { public void run() {
					a.remove();
				}}, 1000);
				entity = a;
			}
			else if (type.equalsIgnoreCase("BOAT")) {
				entity = world.spawn(location, Boat.class);
			}
			else if (type.equalsIgnoreCase("THIS")) {
				entity = thisEnt;
			}
			else if (type.equalsIgnoreCase("CREEPER")) {
				entity = world.spawnCreature(location, CreatureType.CREEPER);
				if (entity == null) {
					throw new YiffBukkitCommandException("Could not spawn a creeper here. Too bright?");
				}
				final Creeper creeper = (Creeper)entity;

				if ("ELECTRIFIED".equalsIgnoreCase(data) || "CHARGED".equalsIgnoreCase(data) || "POWERED".equalsIgnoreCase(data)) {
					creeper.setPowered(true);
				}
			}
			else if (type.equalsIgnoreCase("SLIME")) {
				entity = world.spawnCreature(location, CreatureType.SLIME);
				final Slime slime = (Slime)entity;

				if (data != null) {

					try {
						int size = Integer.parseInt(data);
						slime.setSize(size);
					}
					catch (NumberFormatException e) { }

				}
			}
			else if (type.equalsIgnoreCase("WOLF")) {
				entity = world.spawnCreature(location, CreatureType.WOLF);
				final Wolf wolf = (Wolf)entity;

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
			}
			else if (type.equalsIgnoreCase("SHEEP")) {
				entity = world.spawnCreature(location, CreatureType.SHEEP);
				final Sheep sheep = (Sheep)entity;

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
			}
			else if (type.equalsIgnoreCase("NPC")) {
				final String name = data == null ? "" : data;
				entity = makeNPC(name, location);
			}
			else {
				try {
					CreatureType creatureType = CreatureType.valueOf(type.toUpperCase());
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

	public void checkMobSpawn(CommandSender commandSender, String mobName) throws PermissionDeniedException {
		if (!plugin.permissionHandler.has(commandSender, "yiffbukkit.mobspawn."+mobName.toLowerCase()))
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
