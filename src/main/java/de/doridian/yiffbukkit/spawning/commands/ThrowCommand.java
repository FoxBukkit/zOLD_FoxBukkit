package de.doridian.yiffbukkit.spawning.commands;

import de.doridian.yiffbukkit.main.ToolBind;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.ICommand;
import de.doridian.yiffbukkit.main.commands.ICommand.BooleanFlags;
import de.doridian.yiffbukkit.main.commands.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.ICommand.NumericFlags;
import de.doridian.yiffbukkit.main.commands.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.ICommand.StringFlags;
import de.doridian.yiffbukkit.main.commands.ICommand.Usage;
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
@Usage("[-i <item name or id> ][-m <amount> ][<type>[ <forward>[ <up>[ <left>]]]]")
@BooleanFlags("p")
@StringFlags("i")
@NumericFlags("m")
@Permission("yiffbukkit.throw")
public class ThrowCommand extends ICommand {
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
			ToolBind.remove(ply, toolType);

			PlayerHelper.sendDirectedMessage(ply, "Unbound your tool (\u00a7e"+toolType.name()+"\u00a7f).");

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

		final String typeName = args[0];

		final ToolBind runnable;
		if (typeName.equalsIgnoreCase("me")) {
			plugin.spawnUtils.checkMobSpawn(ply, "me");
			runnable = new ToolBind("/throw me", ply) {
				@Override
				public void run(PlayerInteractEvent event) {
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
				}
			};
		}
		else {
			final String[] types = typeName.split("\\+");
			final double scale = 1/speed.length();

			final int amount;
			final float offset;
			if (numericFlags.containsKey('m')) {
				final int maxItems = ply.hasPermission("yiffbukkit.throw.unlimited") ? 1000 : 10;
				amount = Math.max(0, Math.min(maxItems, (int) (double) numericFlags.get('m')));
				offset = 360.0f / amount;
			}
			else {
				amount = 1;
				offset = 0;
			}

			runnable = new ToolBind("/throw "+typeName, ply) {
				@Override
				public void run(PlayerInteractEvent event) throws YiffBukkitCommandException {
					Player player = event.getPlayer();
					final Location location = player.getEyeLocation();

					float yaw = location.getYaw();

					if (player.isInsideVehicle() && lastYaws.containsKey(player)) {
						yaw = lastYaws.get(player);
						location.setPitch(lastPitches.get(player));
					}

					if (!usePitch)
						location.setPitch(0);

					for (int i = 0; i < amount; ++i) {
						location.setYaw(yaw);
						final Vector direction = Utils.toWorldAxis(location, speed);

						final Location finalLocation = location.clone();
						finalLocation.setX(location.getX()+direction.getX()*scale);
						finalLocation.setY(location.getY()+direction.getY()*scale);
						finalLocation.setZ(location.getZ()+direction.getZ()*scale);

						final Entity entity = plugin.spawnUtils.buildMob(types, player, null, finalLocation);
						entity.setVelocity(direction);

						yaw += offset;
					}
				}
			};
		}

		ToolBind.add(ply, toolType, runnable);

		PlayerHelper.sendDirectedMessage(ply, "Bound \u00a79"+typeName+"\u00a7f to your tool (\u00a7e"+toolType.name()+"\u00a7f). Right-click to use.");
	}
}
