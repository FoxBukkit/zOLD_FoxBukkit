package de.doridian.yiffbukkit.spawning.commands;

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
import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import net.minecraft.server.Packet10Flying;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.Packet;
import org.bukkit.event.server.PacketListener;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;


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
@BooleanFlags("p")
@StringFlags("is")
@NumericFlags("m")
@Permission("yiffbukkit.throw")
public class ThrowCommand extends ICommand {
	public interface ThrowShape {
		Vector getDirection(int i, int amount, Location baseLocation, Vector speed);
	}

	private static final Map<String, ThrowShape> throwShapes = new HashMap<String, ThrowCommand.ThrowShape>();
	static {
		throwShapes.put("circle", new ThrowShape() {
			@Override
			public Vector getDirection(int i, int amount, Location baseLocation, Vector speed) {
				final Location location = baseLocation.clone();

				location.setYaw(location.getYaw() + i * 360.0f / amount);

				return Utils.toWorldAxis(location, speed);
			}
		});

		throwShapes.put("cone", new ThrowShape() {
			@Override
			public Vector getDirection(int i, int amount, Location baseLocation, Vector speed) {
				final Location cone = new Location(null, 0, 0, 0, i * 360.0f / amount, -80);
				final Vector pointingDown = Utils.toWorldAxis(cone, speed);
				
				Location location = baseLocation.clone();
				location.setPitch(location.getPitch()+90);

				return Utils.toWorldAxis(location, pointingDown);
			}
		});

		throwShapes.put("random", new ThrowShape() {
			@Override
			public Vector getDirection(int i, int amount, Location baseLocation, Vector speed) {
				return Utils.randvec().multiply(speed.length());
			}
		});

		throwShapes.put("randomup", new ThrowShape() {
			@Override
			public Vector getDirection(int i, int amount, Location baseLocation, Vector speed) {
				final Vector direction = Utils.randvec().multiply(speed.length());
				direction.setY(Math.abs(direction.getY()));
				return direction;
			}
		});

		throwShapes.put("randomdown", new ThrowShape() {
			@Override
			public Vector getDirection(int i, int amount, Location baseLocation, Vector speed) {
				final Vector direction = Utils.randvec().multiply(speed.length());
				direction.setY(-Math.abs(direction.getY()));
				return direction;
			}
		});
	}

	private final Map<Player, Float> lastYaws = new HashMap<Player, Float>();
	private final Map<Player, Float> lastPitches = new HashMap<Player, Float>();

	public ThrowCommand() {
		final PacketListener packetListener = new PacketListener() {
			@Override
			public boolean onIncomingPacket(Player ply, int packetID, Packet packet) {
				Packet10Flying p10 = (Packet10Flying) packet;
				lastYaws.put(ply, p10.yaw);
				lastPitches.put(ply, p10.pitch);
				return true;
			}

		};

		PacketListener.addPacketListener(false, 12, packetListener, plugin);
		PacketListener.addPacketListener(false, 13, packetListener, plugin);

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

		if (args.length == 0) {
			BindCommand.unbind(ply, toolType);
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
		final String shapeName;
		if (stringFlags.containsKey('s'))
			shapeName = stringFlags.get('s');
		else
			shapeName = "circle";

		final ThrowShape shape = throwShapes.get(shapeName);

		final String typeName = args[0];

		final ToolBind runnable;
		if (typeName.equalsIgnoreCase("me")) {
			plugin.spawnUtils.checkMobSpawn(ply, "me");
			runnable = new ToolBind("/throw me", ply) {
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

					final Entity vehicle = player.getVehicle();
					if (vehicle == null)
						player.setVelocity(direction);
					else
						vehicle.setVelocity(direction);

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
					Player player = event.getPlayer();
					final Location location = player.getEyeLocation();

					if (player.isInsideVehicle() && lastYaws.containsKey(player)) {
						location.setYaw(lastYaws.get(player));
						location.setPitch(lastPitches.get(player));
					}

					if (!usePitch)
						location.setPitch(0);

					for (int i = 0; i < amount; ++i) {
						final Vector direction = shape.getDirection(i, amount, location, speed);
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

		ToolBind.add(ply, toolType, runnable);

		PlayerHelper.sendDirectedMessage(ply, "Bound \u00a79"+typeName+"\u00a7f to your tool (\u00a7e"+toolType.name()+"\u00a7f). Right-click to use.");
	}
}
