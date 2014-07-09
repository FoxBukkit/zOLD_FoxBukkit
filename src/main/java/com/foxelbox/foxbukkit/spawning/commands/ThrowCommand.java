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
package com.foxelbox.foxbukkit.spawning.commands;

import com.foxelbox.foxbukkit.advanced.packetlistener.FBPacketListener;
import com.foxelbox.foxbukkit.core.util.AutoCleanup;
import com.foxelbox.foxbukkit.core.util.PlayerHelper;
import com.foxelbox.foxbukkit.main.FoxBukkitCommandException;
import com.foxelbox.foxbukkit.main.ToolBind;
import com.foxelbox.foxbukkit.main.commands.BindCommand;
import com.foxelbox.foxbukkit.main.commands.system.ICommand;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.BooleanFlags;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Help;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Names;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.NumericFlags;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Permission;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.StringFlags;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Usage;
import com.foxelbox.foxbukkit.main.util.ScheduledTask;
import com.foxelbox.foxbukkit.main.util.Utils;
import com.foxelbox.foxbukkit.spawning.SpawnUtils;
import com.foxelbox.foxbukkit.spawning.fakeentity.FakeEntityParticleSpawner;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayInFlying;
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
@Permission("foxbukkit.throw")
public class ThrowCommand extends ICommand {
	public interface ThrowShape {
		Vector getDirection(int i);
	}

	public interface ThrowShapeFactory {
		ThrowShape createShape(int amount, Location baseLocation, Vector speed, String[] args) throws FoxBukkitCommandException;
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

	private static final Map<String, ThrowShapeFactory> throwShapes = new HashMap<>();
	static {
		throwShapes.put("", new SimpleThrowShapeFactory() {
			@Override
			public Vector getDirection(int i) {
				return Utils.toWorldAxis(baseLocation, speed);
			}
		});

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

						final Location location = baseLocation.clone();
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
			public ThrowShape createShape(int amount, Location baseLocation, Vector speed, String[] args) throws FoxBukkitCommandException {
				final double speedLength = speed.length();

				final double maxDistance = 100;
				final List<Vector> locations = new ArrayList<>();
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
					throw new FoxBukkitCommandException("No valid targets found.");
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

	private final Map<Player, Float> lastYaws = new HashMap<>();
	private final Map<Player, Float> lastPitches = new HashMap<>();

	public ThrowCommand() {
		new FBPacketListener() {
			{
				register(PacketDirection.INCOMING, 12);
				register(PacketDirection.INCOMING, 13);
			}

			@Override
			public boolean onIncomingPacket(Player ply, int packetID, Packet packet) {
				final PacketPlayInFlying p10 = (PacketPlayInFlying) packet;
				lastYaws.put(ply, p10.yaw);
				lastPitches.put(ply, p10.pitch);
				return true;
			}
		};

		AutoCleanup.registerPlayerMap(lastYaws);
		AutoCleanup.registerPlayerMap(lastPitches);
	}

	@Override
	public void Run(Player ply, String[] args, String argStr, String commandName) throws FoxBukkitCommandException {
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

		final Vector speed = new Vector(0,0,2);
		if (args.length >= 2) {
			try {
				speed.setZ(Double.valueOf(args[1]));
				if (args.length >= 3) {
					speed.setY(Double.valueOf(args[2]));
					if (args.length >= 4) {
						speed.setX(Double.valueOf(args[3]));
					}
				}
			}
			catch (NumberFormatException e) {
				throw new FoxBukkitCommandException("Number expected", e);
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
				public boolean run(PlayerInteractEvent event) throws FoxBukkitCommandException {
					final Player player = event.getPlayer();
					final Location location = player.getEyeLocation();
					if (player.isInsideVehicle() && lastYaws.containsKey(player)) {
						location.setYaw(lastYaws.get(player));
						location.setPitch(lastPitches.get(player));
					}

					if (!usePitch)
						location.setPitch(0);

					final Vector direction = shapeFactory.createShape(1, location, speed, shapeArgs).getDirection(0);

					final Entity vehicle = player.isInsideVehicle() ? player.getVehicle() : player;

					vehicle.setVelocity(direction);

					final List<Player> effectTargets = new ArrayList<>();
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
				final int maxItems = ply.hasPermission("foxbukkit.throw.unlimited") ? 1000 : 10;
				amount = Math.max(1, Math.min(maxItems, (int) (double) numericFlags.get('m')));
			}
			else {
				amount = 1;
			}

			runnable = new ToolBind("/throw "+typeName, ply) {
				@Override
				public boolean run(PlayerInteractEvent event) throws FoxBukkitCommandException {
					final Player player = event.getPlayer();
					final Location location = player.getEyeLocation();
					final ThrowShape shape = shapeFactory.createShape(amount, location, speed, shapeArgs);

					if (player.isInsideVehicle() && lastYaws.containsKey(player)) {
						location.setYaw(lastYaws.get(player));
						location.setPitch(lastPitches.get(player));
					}

					if (!usePitch)
						location.setPitch(0);

					SpawnUtils.logSpawn(playerName, location, amount, typeName);
					final double x = location.getX();
					final double y = location.getY();
					final double z = location.getZ();
					for (int i = 0; i < amount; ++i) {
						final Vector direction = shape.getDirection(i);
						// TODO: orientation

						final Location finalLocation = location.clone();
						finalLocation.setX(x + direction.getX()*scale);
						finalLocation.setY(y + direction.getY()*scale);
						finalLocation.setZ(z + direction.getZ()*scale);

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
