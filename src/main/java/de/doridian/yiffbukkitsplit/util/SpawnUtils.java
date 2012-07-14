package de.doridian.yiffbukkitsplit.util;

import de.doridian.yiffbukkit.main.PermissionDeniedException;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.ICommand;
import de.doridian.yiffbukkit.main.util.ScheduledTask;
import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import de.doridian.yiffbukkit.spawning.fakeentity.FakeEntity;
import de.doridian.yiffbukkit.spawning.fakeentity.FakeExperienceOrb;
import de.doridian.yiffbukkit.spawning.fakeentity.FakeVehicle;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.LinkedList;

public class SpawnUtils {
	private YiffBukkit plugin;
	/**
	 * Entities that already have an effect attached to them.
	 */
	private Set<Entity> hasEffect = new HashSet<Entity>();

	private static final int[] randomCrap = {
		60,
		61,
		62,
		63,
		64,
		65,
		72,
		73,
		90,
	};

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
					notchEntity = new AreaCustomPotion(location, 12, notchPlayer, 3) {
						@Override
						protected void directHit(Entity entity) {
							if (!(entity instanceof LivingEntity))
								return;

							plugin.playerHelper.rage((LivingEntity) entity, 100);
						}

						@Override
						protected void areaHit(Entity entity) {
							if (!(entity instanceof LivingEntity))
								return;

							plugin.playerHelper.rage((LivingEntity) entity, 75);
						}
					};
				}
				else if ("LSD".equalsIgnoreCase(data)) {
					// TODO: pick a new color
					notchEntity = new AreaCustomPotion(location, 3, notchPlayer, 3) {
						@Override
						protected void areaHit(final Entity entity) {
							if (!(entity instanceof Player))
								return;

							final Player player = (Player) entity;

							if (!hasEffect.add(player))
								return;

							new ScheduledTask(plugin) {
								int i = 0;
								Queue<Entity> toRemove = new LinkedList<Entity>();
								@Override
								public void run() {
									if (i == 500) {
										for (Entity e : toRemove) {
											e.remove();
										}
										hasEffect.remove(entity);
										cancel();
										return;
									}

									for (int j = 0; j < 5; ++j) {
										final Location currentLocation = player.getLocation().clone().add(Math.random()*10-5, -1, Math.random()*10-5);
										final FakeEntity fakeEntity = new FakeVehicle(currentLocation, randomCrap[(int) Math.floor(Math.random()*randomCrap.length)]);
										fakeEntity.send(player);
										fakeEntity.teleport(currentLocation);
										fakeEntity.setVelocity(new Vector(0,.3+Math.random(),0));
										toRemove.add(fakeEntity);
									}
									for (int j = 0; j < 3; ++j) {
										final Vector velocity = Utils.randvec().multiply(1+Math.random());
										final Location currentLocation = player.getEyeLocation().subtract(velocity.clone().multiply(20));
										final FakeEntity fakeEntity = new FakeVehicle(currentLocation, 72);
										fakeEntity.send(player);
										fakeEntity.teleport(currentLocation);
										fakeEntity.setVelocity(velocity);
										toRemove.add(fakeEntity);
									}
									while (toRemove.size() > 240) {
										toRemove.poll().remove();
									}

									++i;
								}
							}.scheduleSyncRepeating(0, 1);
						}
					};
				}
				else if ("ROCKET".equalsIgnoreCase(data)) {
					// TODO: pick a new color
					notchEntity = new AreaCustomPotion(location, 12, notchPlayer, 3) {
						@Override
						protected void areaHit(final Entity entity) {
							if (!(entity instanceof LivingEntity))
								return;

							if (entity instanceof Player)
								return;

							if (!hasEffect.add(entity))
								return;

							final double maxHeight = entity.getLocation().getY()+32;
							new ScheduledTask(plugin) {
								int i = 0;
								List<Entity> toRemove = new ArrayList<Entity>();
								Vector up = entity.getVelocity();
								@Override
								public void run() {
									if (i == 101) {
										for (Entity e : toRemove) {
											e.remove();
										}
										hasEffect.remove(entity);
										return;
									}

									up = up.add(new Vector(0,0.1,0));
									entity.setVelocity(up);
									final Location currentLocation = entity.getLocation();
									//for (int data = 0; data < 16; ++data)
									final World currentWorld = currentLocation.getWorld();
									currentWorld.playEffect(currentLocation, Effect.SMOKE, 4);
									currentWorld.playEffect(currentLocation, Effect.EXTINGUISH, 0);

									++i;
									if (i == 100 || currentLocation.getY() >= maxHeight) {
										i = 101;
										entity.remove();
										for (Player player : currentWorld.getPlayers()) {
											final Location playerLocation = player.getLocation();
											if (currentLocation.distanceSquared(playerLocation) > 64*64)
												continue;

											final Location modifiedLocation = playerLocation.add(currentLocation).multiply(0.5);
											player.playEffect(modifiedLocation, Effect.ZOMBIE_DESTROY_DOOR, 0);
										}
										cancel();

										for (int i = 0; i < 100; ++i) {
											final FakeEntity fakeEntity = new FakeExperienceOrb(currentLocation, 1);
											fakeEntity.send();
											fakeEntity.teleport(currentLocation);
											fakeEntity.setVelocity(Utils.randvec());
											toRemove.add(fakeEntity);
										}

										scheduleSyncDelayed(60);
									}
								}
							}.scheduleSyncRepeating(0, 1);
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

							plugin.playerHelper.vanish(ICommand.asPlayer(commandSender));

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
				entity = world.spawnEntity(location, EntityType.CREEPER);
				if (entity == null) {
					throw new YiffBukkitCommandException("Could not spawn a creeper here. Too bright?");
				}
				final Creeper creeper = (Creeper)entity;

				if ("ELECTRIFIED".equalsIgnoreCase(data) || "CHARGED".equalsIgnoreCase(data) || "POWERED".equalsIgnoreCase(data)) {
					creeper.setPowered(true);
				}
			}
			else if (type.equalsIgnoreCase("SLIME")) {
				entity = world.spawnEntity(location, EntityType.SLIME);
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
				entity = world.spawnEntity(location, EntityType.WOLF);
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
				entity = world.spawnEntity(location, EntityType.SHEEP);
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
			else if(type.equalsIgnoreCase("OCELOT") || type.equalsIgnoreCase("CAT")) {
				entity = world.spawnEntity(location, EntityType.OCELOT);

				final Ocelot ocelot = (Ocelot)entity;
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
			}
			else {
				try {
					EntityType creatureType = EntityType.valueOf(type.toUpperCase());
					entity = world.spawnEntity(location, creatureType);
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

			if (movingobjectposition.entity != null) {
				directHit(movingobjectposition.entity.getBukkitEntity());
			}

			final Location thisLocation = thisBukkitEntity.getLocation();

			for (Entity entity: thisBukkitEntity.getNearbyEntities(radius, radius, radius)) {
				if (entity.getLocation().distanceSquared(thisLocation) > radius*radius)
					continue;

				if (entity.equals(thrower))
					continue;

				if (entity.equals(movingobjectposition.entity))
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
