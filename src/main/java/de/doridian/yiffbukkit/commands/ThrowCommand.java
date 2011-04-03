package de.doridian.yiffbukkit.commands;

import net.minecraft.server.EntityFallingSand;
import net.minecraft.server.EntityPig;
import net.minecraft.server.EntityTNTPrimed;
import net.minecraft.server.EntityWolf;
import net.minecraft.server.WorldServer;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftFallingSand;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.entity.CraftTNTPrimed;
import org.bukkit.craftbukkit.entity.CraftWolf;
import org.bukkit.entity.Boat;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Wolf;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import de.doridian.yiffbukkit.ToolBind;
import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.sheep.CamoSheep;
import de.doridian.yiffbukkit.sheep.PartySheep;

public class ThrowCommand extends ICommand {
	public ThrowCommand(YiffBukkit plug) {
		super(plug);
	}

	@Override
	public int GetMinLevel() {
		return 4;
	}

	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		Material toolType = ply.getItemInHand().getType();

		if (argStr.isEmpty()) {
			playerHelper.addToolMapping(ply, toolType, null);

			playerHelper.SendDirectedMessage(ply, "Unbound your current tool (§e"+toolType.name()+"§f).");

			return;
		}

		double speed = 2;
		if (args.length >= 2) {
			try {
				speed = Double.valueOf(args[1]);
			} catch (NumberFormatException e) {
				throw new YiffBukkitCommandException("Number expected", e);
			}
		}
		final double finalSpeed = speed;

		String typeName = args[0].toUpperCase();

		ToolBind runnable;
		if (typeName.equals("ME")) {
			runnable = new ToolBind("/throw me", ply) {
				public void run(PlayerInteractEvent event) {
					Player player = event.getPlayer();
					final Location location = player.getLocation();

					final Vector direction = location.getDirection();
					if (player.isInsideVehicle()) {
						Entity vehicle = ((CraftPlayer)player).getHandle().vehicle.getBukkitEntity();//ply.getVehicle()
						vehicle.setVelocity(location.getDirection().multiply(finalSpeed));
					}
					else {
						player.setVelocity(direction.multiply(finalSpeed));
					}
				}
			};
		}
		else {
			final String[] types = typeName.split("\\+");

			runnable = new ToolBind("/throw "+typeName, ply) {
				public void run(PlayerInteractEvent event) {
					Player player = event.getPlayer();
					Entity previous = null;
					final World world = player.getWorld();
					final WorldServer notchWorld = ((CraftWorld)world).getHandle();
					final Location location = player.getEyeLocation();
					for (String part : types) {
						String[] partparts = part.split(":");

						String type = partparts[0];
						String data = partparts.length >= 2 ? partparts[1] : null;

						Entity entity;
						if (type.equals("ME")) {
							entity = player;
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
								playerHelper.SendDirectedMessage(player, "You must face a creature/boat/minecart");
								return;
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
										eWolf.a(playerName);
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
								playerHelper.SendDirectedMessage(player, "Creature type "+type+" not found");
								return;
							}
						}

						if (entity == null) {
							playerHelper.SendDirectedMessage(player, "Failed to spawn "+type);
							return;
						}

						if (previous == null) {
							entity.setVelocity(location.getDirection().multiply(finalSpeed));
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
				}
			};
		}

		playerHelper.addToolMapping(ply, toolType, runnable);

		playerHelper.SendDirectedMessage(ply, "Bound §9"+typeName+"§f to your current tool (§e"+toolType.name()+"§f). Right-click to use.");
	}

	@Override
	public String GetHelp() {
		return
		"Binds creature/tnt/sand/gravel/minecart/self('me')/target('this') throwing to your current tool. Right-click to use.\n"+
		"Unbind by typing '/throw' without arguments. You can stack mobs by separating them with a plus (+).\n"+
		"Data values:\n"+
		"  sheep:<dye color>|party|camo\n"+
		"  wolf:angry|tame|sit (can be combined)";
	}

	@Override
	public String GetUsage() {
		return "[<type> [<speed>]]";
	}
}
