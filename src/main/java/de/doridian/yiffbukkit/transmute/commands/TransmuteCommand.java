package de.doridian.yiffbukkit.transmute.commands;

import de.doridian.yiffbukkit.main.PermissionDeniedException;
import de.doridian.yiffbukkit.main.ToolBind;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.ICommand;
import de.doridian.yiffbukkit.spawning.commands.GiveCommand;
import de.doridian.yiffbukkit.main.commands.ICommand.BooleanFlags;
import de.doridian.yiffbukkit.main.commands.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.ICommand.StringFlags;
import de.doridian.yiffbukkit.main.commands.ICommand.Usage;
import de.doridian.yiffbukkit.transmute.EntityShape;
import de.doridian.yiffbukkit.transmute.Shape;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;

@Names("transmute")
@Help(
		"Disguises you or an entity as a mob.\n" +
		"Flags:\n" +
		"  -e to transmute an entity (binds to a tool)\n" +
		"  -i <item name or id> together with -e to bind to a specific tool.\n" +
		"  -l to transmute the last entity you transmuted"
)
@Usage("[<flags>][<shape>]")
@Permission("yiffbukkitsplit.transmute")
@BooleanFlags("el")
@StringFlags("i")
public class TransmuteCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		args = parseFlags(args);

		if (args.length == 0) {
			final Entity target;
			if (booleanFlags.contains('l')) {
				target = plugin.transmute.getLastTransmutedEntity(ply);
			}
			else {
				target = ply;
			}

			if (!plugin.transmute.isTransmuted(target))
				throw new YiffBukkitCommandException("Not transmuted");

			plugin.transmute.resetShape(ply, target);

			if (ply == target) {
				playerHelper.sendDirectedMessage(ply, "Transmuted you back into your original shape.");
			}
			else {
				playerHelper.sendDirectedMessage(ply, "Transmuted your last target back into its original shape.");
			}

			if (!(target instanceof Player) || !plugin.vanish.isVanished((Player) target)) {
				effect(target, null);
			}
			return;
		}

		final String mobType = args[0];
		if (booleanFlags.contains('e')) {
			if (!plugin.permissionHandler.has(ply, "yiffbukkitsplit.transmute.others"))
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
					if (!plugin.permissionHandler.has(player, "yiffbukkitsplit.transmute.others"))
						throw new PermissionDeniedException();

					final Entity entity = event.getRightClicked();

					final Shape shape;
					if (plugin.transmute.isTransmuted(entity)) {
						shape = null;
						plugin.transmute.resetShape(player, entity);

						playerHelper.sendDirectedMessage(player, "Transmuted your target back into its original shape.");
					}
					else {
						shape = plugin.transmute.setShape(player, entity , mobType);

						playerHelper.sendDirectedMessage(player, "Transmuted your target into a "+mobType+".");
					}

					effect(entity, shape);
				}
			});

			playerHelper.sendDirectedMessage(ply, "Bound \u00a79"+mobType+"\u00a7f to your tool (\u00a7e"+toolType.name()+"\u00a7f). Right-click an entity to use.");
			return;
		}

		final Entity target;
		if (booleanFlags.contains('l')) {
			target = plugin.transmute.getLastTransmutedEntity(ply);
		}
		else {
			target = ply;
		}

		final Shape shape = plugin.transmute.setShape(ply, target, mobType);

		if (ply == target) {
			playerHelper.sendDirectedMessage(ply, "Transmuted you into "+mobType+".");
		}
		else {
			playerHelper.sendDirectedMessage(ply, "Transmuted your last target into "+mobType+".");
		}

		if (!(target instanceof Player) || !plugin.vanish.isVanished((Player) target)) {
			effect(target, shape);
		}
	}

	private void effect(Entity target, Shape shape) {
		final Location location;
		if (target instanceof LivingEntity) {
			location = ((LivingEntity) target).getEyeLocation();
		}
		else {
			location = target.getLocation();
			if (shape instanceof EntityShape)
				location.add(0, ((EntityShape) shape).getYOffset(), 0);
		}
		effect(location);
	}

	private static final void effect(Location location) {
		final World world = location.getWorld();

		world.playEffect(location, Effect.EXTINGUISH, 0);
		world.playEffect(location, Effect.SMOKE, 4);
		world.playEffect(location, Effect.SMOKE, 4);
		world.playEffect(location, Effect.SMOKE, 4);
	}
}
