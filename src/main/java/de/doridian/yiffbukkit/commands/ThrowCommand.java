package de.doridian.yiffbukkit.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import de.doridian.yiffbukkit.ToolBind;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.YiffBukkitPlayerListener;

public class ThrowCommand extends ICommand {
	public ThrowCommand(YiffBukkitPlayerListener playerListener) {
		super(playerListener);
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
				public void run(PlayerInteractEvent event) throws YiffBukkitCommandException {
					Player player = event.getPlayer();
					final Location location = player.getEyeLocation();
					Entity entity = plugin.utils.buildMob(types, player, null, location);
					entity.setVelocity(location.getDirection().multiply(finalSpeed));

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
