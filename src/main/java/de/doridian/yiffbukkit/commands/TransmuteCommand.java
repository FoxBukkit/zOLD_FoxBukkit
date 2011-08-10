package de.doridian.yiffbukkit.commands;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import de.doridian.yiffbukkit.PermissionDeniedException;
import de.doridian.yiffbukkit.ToolBind;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("transmute")
@Help(
		"Disguises you or an entity as a mob.\n" +
		"Flags:\n" +
		"  -e to transmute an entity (binds to a tool)\n" +
		"  -i <item name or id> together with -e to bind to a specific tool."
)
@Usage("[<flags>][<shape>]")
@Permission("yiffbukkit.transmute")
@BooleanFlags("e")
@StringFlags("i")
public class TransmuteCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		args = parseFlags(args);

		if (args.length == 0) {
			if (!plugin.transmute.isTransmuted(ply))
				throw new YiffBukkitCommandException("Not transmuted");

			plugin.transmute.resetShape(ply);

			playerHelper.sendDirectedMessage(ply, "Transmuted you back into your original shape.");
			return;
		}

		final String mobType = args[0];
		if (booleanFlags.contains('e')) {
			if (!plugin.permissionHandler.has(ply, "yiffbukkit.transmute.others"))
				throw new PermissionDeniedException();

			final Material toolType;
			if (stringFlags.containsKey('i')) {
				final String materialName = stringFlags.get('i');
				toolType = GiveCommand.matchMaterial(materialName);
			}
			else {
				toolType = ply.getItemInHand().getType();
			}
			playerHelper.addToolMapping(ply, toolType, new ToolBind(mobType, ply) {
				@Override
				public void run(PlayerInteractEntityEvent event) throws YiffBukkitCommandException {
					final Player player = event.getPlayer();
					if (!plugin.permissionHandler.has(player, "yiffbukkit.transmute.others"))
						throw new PermissionDeniedException();

					Entity entity = event.getRightClicked();
					plugin.transmute.setShape(player, entity , mobType);

					playerHelper.sendDirectedMessage(player, "Transmuted your target into a "+mobType+".");

					final Location location;
					if (entity instanceof LivingEntity)
						location = ((LivingEntity) entity).getEyeLocation();
					else
						location = entity.getLocation();

					effect(location);
				}
			});

			playerHelper.sendDirectedMessage(ply, "Bound §9"+mobType+"§f to your tool (§e"+toolType.name()+"§f). Right-click an entity to use.");
			return;
		}

		plugin.transmute.setShape(ply, ply, mobType);

		playerHelper.sendDirectedMessage(ply, "Transmuted you into "+mobType+".");

		if (!plugin.vanish.isVanished(ply)) {
			effect(ply.getEyeLocation());
		}
	}

	private void effect(final Location location) {
		final World world = location.getWorld();

		world.playEffect(location, Effect.EXTINGUISH, 0);
		world.playEffect(location, Effect.SMOKE, 4);
		world.playEffect(location, Effect.SMOKE, 4);
		world.playEffect(location, Effect.SMOKE, 4);
	}
}
