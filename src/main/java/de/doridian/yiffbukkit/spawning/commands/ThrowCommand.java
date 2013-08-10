package de.doridian.yiffbukkit.spawning.commands;

import de.doridian.yiffbukkit.advanced.packetlistener.YBPacketListener;
import de.doridian.yiffbukkit.main.ToolBind;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.BindCommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.BooleanFlags;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.NumericFlags;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.StringFlags;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;
import de.doridian.yiffbukkit.main.util.ScheduledTask;
import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkit.spawning.SpawnUtils;
import de.doridian.yiffbukkit.spawning.fakeentity.FakeEntityParticleSpawner;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import net.minecraft.server.v1_6_R2.Packet;
import net.minecraft.server.v1_6_R2.Packet10Flying;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Names("throw")
@Help(
		"Binds throwing a creature/tnt/sand/gravel/minecart\n"+
		"or yourself('me') or your target('this') to your\n"+
		"selected tool. Right-click to use.\n"+
		"Unbind by typing '/throw' without arguments.\n" +
		"You can stack mobs by separating them with a plus (+).\n"+
		"Data values:\n"+
		"  sheep:<dye color>|party|camo|sheared\n"+
		"  wolf:angry|tame|sit - can be combined with a comma (,)\n"+
		"  creeper:charged"
)
@Usage("[-i <item name or id> ][-m <amount> [-s <shape> ]][<type>[ <forward>[ <up>[ <left>]]]]")
@BooleanFlags("px")
@StringFlags("is")
@NumericFlags("m")
@Permission("yiffbukkit.throw")
public class ThrowCommand extends ICommand {
	public interface ThrowShape {
		Vector getDirection(int i);
	}

	public interface ThrowShapeFactory {
		ThrowShape createShape(int amount, Location baseLocation, Vector speed, String[] args) throws YiffBukkitCommandException;
	}

	public static abstract class SimpleThrowShapeFactory implements ThrowShape, ThrowShapeFactory {
		protected int amount;
		protected Location baseLocation;
		protected Vector speed;
		@Override
		public final ThrowShape createShape(int amount, Location baseLocation, Vector speed, String[] args) {
			this.amount = amount;
			this.baseLocation = baseLocation;
			this.speed = speed;
			return this;
		}
	}

	private static final Map<String, ThrowShapeFactory> throwShapes = new HashMap<String, ThrowShapeFactory>();
	static {
		throwShapes.put("circle", new SimpleThrowShapeFactory() {
			@Override
			public Vector getDirection(int i) {
				final Location location = baseLocation.clone();

				location.setYaw(location.getYaw() + i * 360.0f / amount);

				return Utils.toWorldAxis(location, speed);
			}
		});

		throwShapes.put("cone", new ThrowShapeFactory() {
			@Override
			public ThrowShape createShape(final int amount, final Location baseLocation, final Vector speed, String[] args) {
				final float angle;
				if (args.length >= 2) {
					angle = Float.parseFloat(args[1]);
				}
				else {
					angle = 10;
				}

				return new ThrowShape() {
					@Override
					public Vector getDirection(int i) {
						final Location cone = new Location(null, 0, 0, 0, i * 360.0f / amount, -90+angle);
						final Vector pointingDown = Utils.toWorldAxis(cone, speed);

						Location location = baseLocation.clone();
						location.setPitch(location.getPitch()+90);

						return Utils.toWorldAxis(location, pointingDown);
					}
				};
			}
		});

		throwShapes.put("rcone", new ThrowShapeFactory() {
			@Override
			public ThrowShape createShape(final int amount, final Location baseLocation, final Vector speed, String[] args) {
				final float angle;
				if (args.length >= 2) {
					angle = Float.parseFloat(args[1]);
				}
				else {
					angle = 10;
				}

				return new ThrowShape() {
					@Override
					public Vector getDirection(int i) {
						return Utils.randomCone(baseLocation, angle).multiply(speed.length());
					}
				};
			}
		});

		throwShapes.put("random", new SimpleThrowShapeFactory() {
			@Override
			public Vector getDirection(int i) {
				return Utils.randvec().multiply(speed.length());
			}
		});

		throwShapes.put("randomup", new SimpleThrowShapeFactory() {
			@Override
			public Vector getDirection(int i) {
				final Vector direction = Utils.randvec().multiply(speed.length());
				direction.setY(Math.abs(direction.getY()));
				return direction;
			}
		});

		throwShapes.put("randomdown", new SimpleThrowShapeFactory() {
			@Override
			public Vector getDirection(int i) {
				final Vector direction = Utils.randvec().multiply(speed.length());
				direction.setY(-Math.abs(direction.getY()));
				return direction;
			}
		});

		throwShapes.put("near", new ThrowShapeFactory() {
			@Override
			public ThrowShape createShape(int amount, Location baseLocation, Vector speed, String[] args) throws YiffBukkitCommandException {
				final double speedLength = speed.length();

				final double maxDistance = 100;
				final List<Vector> locations = new ArrayList<Vector>();
				for (LivingEntity entity : baseLocation.getWorld().getLivingEntities()) {
					if (entity instanceof Player)
						continue;

					final Location entityLocation = entity.getLocation();
					final Vector subtract = entityLocation.subtract(baseLocation).toVector();
					final double distanceSq = subtract.lengthSquared();

					if (distanceSq > maxDistance*maxDistance)
						continue;

					locations.add(subtract);
				}

				Collections.sort(locations, new Comparator<Vector>() {
					@Override
					public int compare(Vector o1, Vector o2) {
						return Double.compare(o1.lengthSquared(), o2.lengthSquared());
					}
				});

				if (locations.isEmpty()) {
					throw new YiffBukkitCommandException("No valid targets found.");
				}

				return new ThrowShape() {
					@Override
					public Vector getDirection(int i) {
						return locations.get(i % locations.size()).normalize().multiply(speedLength);
					}
				};
			}
		});
	}

	private final Map<Player, Float> lastYaws = new HashMap<Player, Float>();
	private final Map<Player, Float> lastPitches = new HashMap<Player, Float>();

	public ThrowCommand() {
		new YBPacketListener() {
			{
				register(PacketDirection.INCOMING, 12);
				register(PacketDirection.INCOMING, 13);
			}

			@Override
			public boolean onIncomingPacket(Player ply, int packetID, Packet packet) {
				Packet10Flying p10 = (Packet10Flying) packet;
				lastYaws.put(ply, p10.yaw);
				lastPitches.put(ply, p10.pitch);
				return true;
			}
		};

		playerHelper.registerMap(lastYaws);
		playerHelper.registerMap(lastPitches);
	}

	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		args = parseFlags(args);

		final Material toolType;
		if (stringFlags.containsKey('i')) {
			final String materialName = stringFlags.get('i');
			toolType = GiveCommand.matchMaterial(materialName);
		}
		else {
			toolType = ply.getItemInHand().getType();
		}

		boolean left = booleanFlags.contains('x');

		if (args.length == 0) {
			BindCommand.unbind(ply, toolType, left);
			return;
		}

		final Vector speed = new Vector(2,0,0);
		if (args.length >= 2) {
			try {
				speed.setX(Double.valueOf(args[1]));
				if (args.length >= 3) {
					speed.setY(Double.valueOf(args[2]));
					if (args.length >= 4) {
						speed.setZ(Double.valueOf(args[3]));
					}
				}
			}
			catch (NumberFormatException e) {
				throw new YiffBukkitCommandException("Number expected", e);
			}
		}

		final boolean usePitch = !booleanFlags.contains('p');
		String shapeString = "circle";
		if (stringFlags.containsKey('s')) {
			shapeString = stringFlags.get('s');
		}

		final String[] shapeArgs = shapeString.split(":");

		final String shapeName = shapeArgs[0];

		final ThrowShapeFactory shapeFactory = throwShapes.get(shapeName);

		final String typeName = args[0];

		final ToolBind runnable;
		final Pattern pattern = Pattern.compile("^[mM][eE](?::([a-zA-Z0-9]+)(?::(.+))?)?$");
		final Matcher matcher = pattern.matcher(typeName);
		if (matcher.matches()) {
			final String effectType = matcher.group(1);
			final String data = matcher.group(2);

			plugin.spawnUtils.checkMobSpawn(ply, "me");
			runnable = new ToolBind("/throw me", ply) {
				long nextFlap = Long.MIN_VALUE;
				@Override
				public boolean run(PlayerInteractEvent event) {
					final Player player = event.getPlayer();
					final Location location = player.getEyeLocation();
					if (player.isInsideVehicle() && lastYaws.containsKey(player)) {
						location.setYaw(lastYaws.get(player));
						location.setPitch(lastPitches.get(player));
					}

					if (!usePitch)
						location.setPitch(0);

					final Vector direction = Utils.toWorldAxis(location, speed);

					final Entity vehicle = player.isInsideVehicle() ? player.getVehicle() : player;

					vehicle.setVelocity(direction);

					final List<Player> effectTargets = new ArrayList<Player>();
					for (Player effectTarget : Utils.getObservingPlayers(player)) {
						if (effectTarget.getLocation().distanceSquared(location) > 64*64)
							continue;

						effectTargets.add(effectTarget);
					}

					final FakeEntityParticleSpawner spawner;

					switch (effectType == null ? "null" : effectType.toLowerCase()) {
					case "wings":
					case "wing":
					case "flap":
						final long t = System.currentTimeMillis();
						if (t >= nextFlap) {
							for (Player effectTarget : effectTargets) {
								Utils.makeSound(vehicle.getLocation(), "mob.enderdragon.wings", 4.0f, 0.0f, effectTarget);
							}
							nextFlap = t + 800 + (long) (Math.random() * 400);
						}

						return true;

					case "noisy":
					case "noise":
					case "rocket":
						if (data == null) {
							spawner = SpawnUtils.makeParticleSpawner(vehicle.getLocation(), "fireworksSpark", 10, 0.05, new Vector());
						}
						else {
							spawner = SpawnUtils.makeParticleSpawner(vehicle.getLocation(), "fireworksSpark:"+data, 10, 0.05, new Vector());
						}
						break;

					case "particle":
						if (data == null)
							return true;

						spawner = SpawnUtils.makeParticleSpawner(vehicle.getLocation(), data, 3, 0, new Vector());

						break;

					default:
						return true;
					}

					final long endTime = System.currentTimeMillis() + 200;
					final ScheduledTask runnable = new ScheduledTask(plugin) {
						@Override
						public void run() {
							if (System.currentTimeMillis() >= endTime) {
								cancel();
								return;
							}

							final Location effectLocation = vehicle.getLocation();
							spawner.teleport(effectLocation);
							for (Player effectTarget : effectTargets) {
								effectTarget.playEffect(effectLocation, Effect.EXTINGUISH, 0);
								spawner.send(effectTarget);
							}
						}
					};
					runnable.run();
					runnable.scheduleSyncRepeating(0, 1);

					return true;
				}
			};
		}
		else {
			final String[] types = typeName.split("\\+");
			final double scale = 1/speed.length();

			final int amount;
			if (numericFlags.containsKey('m')) {
				final int maxItems = ply.hasPermission("yiffbukkit.throw.unlimited") ? 1000 : 10;
				amount = Math.max(1, Math.min(maxItems, (int) (double) numericFlags.get('m')));
			}
			else {
				amount = 1;
			}

			runnable = new ToolBind("/throw "+typeName, ply) {
				@Override
				public boolean run(PlayerInteractEvent event) throws YiffBukkitCommandException {
					final Player player = event.getPlayer();
					final Location location = player.getEyeLocation();
					final ThrowShape shape = shapeFactory.createShape(amount, location, speed, shapeArgs);

					if (player.isInsideVehicle() && lastYaws.containsKey(player)) {
						location.setYaw(lastYaws.get(player));
						location.setPitch(lastPitches.get(player));
					}

					if (!usePitch)
						location.setPitch(0);

					for (int i = 0; i < amount; ++i) {
						final Vector direction = shape.getDirection(i);
						// TODO: orientation

						final Location finalLocation = location.clone();
						finalLocation.setX(location.getX()+direction.getX()*scale);
						finalLocation.setY(location.getY()+direction.getY()*scale);
						finalLocation.setZ(location.getZ()+direction.getZ()*scale);

						final Entity entity = plugin.spawnUtils.buildMob(types, player, player, finalLocation);
						entity.setVelocity(direction);
					}

					return true;
				}
			};
		}

		ToolBind.add(ply, toolType, left, runnable);

		PlayerHelper.sendDirectedMessage(ply, "Bound \u00a79"+typeName+"\u00a7f to your tool (\u00a7e"+toolType.name()+"\u00a7f). Right-click to use.");
	}
}
