package de.doridian.yiffbukkit.commands;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.server.Packet10Flying;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.Packet;
import org.bukkit.event.server.PacketListener;
import org.bukkit.util.Vector;

import de.doridian.yiffbukkit.ToolBind;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;
import de.doridian.yiffbukkit.util.Utils;


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
@Usage("[-i <item name or id>][<type>[ <forward>[ <up>[ <left>]]]]")
@Level(3)
@StringFlags("i")
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
			playerHelper.addToolMapping(ply, toolType, null);

			playerHelper.sendDirectedMessage(ply, "Unbound your tool (§e"+toolType.name()+"§f).");

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

		String typeName = args[0];

		ToolBind runnable;
		if (typeName.equalsIgnoreCase("me")) {
			plugin.utils.checkMobSpawn(ply, "me");
			runnable = new ToolBind("/throw me", ply) {
				@Override
				public void run(PlayerInteractEvent event) {
					Player player = event.getPlayer();
					final Location location = player.getEyeLocation();
					if (player.isInsideVehicle() && lastYaws.containsKey(player)) {
						location.setYaw(lastYaws.get(player));
						location.setPitch(lastPitches.get(player));
					}
					Vector direction = Utils.toWorldAxis(location, speed);

					if (player.isInsideVehicle()) {
						Entity vehicle = ((CraftPlayer)player).getHandle().vehicle.getBukkitEntity();//ply.getVehicle()
						vehicle.setVelocity(direction);
					}
					else {
						player.setVelocity(direction);
					}
				}
			};
		}
		else {
			final String[] types = typeName.split("\\+");
			final double scale = 1/speed.length();

			runnable = new ToolBind("/throw "+typeName, ply) {
				@Override
				public void run(PlayerInteractEvent event) throws YiffBukkitCommandException {
					Player player = event.getPlayer();
					final Location location = player.getEyeLocation();
					if (player.isInsideVehicle() && lastYaws.containsKey(player)) {
						location.setYaw(lastYaws.get(player));
						location.setPitch(lastPitches.get(player));
					}
					final Vector direction = Utils.toWorldAxis(location, speed);

					location.setX(location.getX()+direction.getX()*scale);
					location.setY(location.getY()+direction.getY()*scale);
					location.setZ(location.getZ()+direction.getZ()*scale);
					Entity entity = plugin.utils.buildMob(types, player, null, location);
					entity.setVelocity(direction);

				}
			};
		}

		playerHelper.addToolMapping(ply, toolType, runnable);

		playerHelper.sendDirectedMessage(ply, "Bound §9"+typeName+"§f to your tool (§e"+toolType.name()+"§f). Right-click to use.");
	}
}
