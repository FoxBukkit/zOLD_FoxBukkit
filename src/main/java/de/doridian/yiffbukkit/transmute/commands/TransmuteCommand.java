package de.doridian.yiffbukkit.transmute.commands;

import de.doridian.yiffbukkit.main.PermissionDeniedException;
import de.doridian.yiffbukkit.main.ToolBind;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.BooleanFlags;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.StringFlags;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;
import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkit.spawning.SpawnUtils;
import de.doridian.yiffbukkit.spawning.commands.GiveCommand;
import de.doridian.yiffbukkit.transmute.EntityShape;
import de.doridian.yiffbukkit.transmute.Shape;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.util.Vector;

import java.util.List;

@Names("transmute")
@Help(
		"Disguises you or an entity as a mob.\n" +
		"Flags:\n" +
		"  -e to transmute an entity (binds to a tool)\n" +
		"  -i <item name or id> together with -e to bind to a specific tool\n" +
		"  -l to transmute the last entity you transmuted\n" +
		"  -x to bind to the left instead of the right mouse button"
)
@Usage("[<flags>][<shape>]")
@Permission("yiffbukkit.transmute")
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
				PlayerHelper.sendDirectedMessage(ply, "Transmuted you back into your original shape.");
			}
			else {
				PlayerHelper.sendDirectedMessage(ply, "Transmuted your last target back into its original shape.");
			}

			effect(target, null);
			return;
		}

		final String mobType = args[0];
		if (booleanFlags.contains('e')) {
			if (!ply.hasPermission("yiffbukkit.transmute.others"))
				throw new PermissionDeniedException();

			final Material toolType;
			if (stringFlags.containsKey('i')) {
				final String materialName = stringFlags.get('i');
				toolType = GiveCommand.matchMaterial(materialName);
			}
			else {
				toolType = ply.getItemInHand().getType();
			}

			boolean left = booleanFlags.contains('x');

			ToolBind.add(ply, toolType, left, new ToolBind(mobType, ply) {
				@Override
				public boolean run(PlayerInteractEntityEvent event) throws YiffBukkitCommandException {
					final Player player = event.getPlayer();
					if (!player.hasPermission("yiffbukkit.transmute.others"))
						throw new PermissionDeniedException();

					final Entity entity = event.getRightClicked();

					final Shape shape;
					if (plugin.transmute.isTransmuted(entity)) {
						shape = null;
						plugin.transmute.resetShape(player, entity);

						PlayerHelper.sendDirectedMessage(player, "Transmuted your target back into its original shape.");
					}
					else {
						shape = plugin.transmute.setShape(player, entity , mobType);

						PlayerHelper.sendDirectedMessage(player, "Transmuted your target into a "+mobType+".");
					}

					effect(entity, shape);
					return true;
				}
			});

			PlayerHelper.sendDirectedMessage(ply, "Bound \u00a79"+mobType+"\u00a7f to your tool (\u00a7e"+toolType.name()+"\u00a7f). Right-click an entity to use.");
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
			PlayerHelper.sendDirectedMessage(ply, "Transmuted you into "+mobType+".");
		}
		else {
			PlayerHelper.sendDirectedMessage(ply, "Transmuted your last target into "+mobType+".");
		}

		effect(target, shape);
	}

	private void effect(Entity target, Shape shape) {
		Location location;
		if (target instanceof LivingEntity) {
			location = ((LivingEntity) target).getEyeLocation();
		}
		else {
			location = target.getLocation();
			if (shape instanceof EntityShape)
				location = location.add(0, ((EntityShape) shape).getYOffset(), 0);
		}

		double radius = 64;
		radius *= radius;

		final List<Player> players;
		if (target instanceof Player)
			players = Utils.getObservingPlayers((Player) target);
		else
			players = target.getWorld().getPlayers();

		for (Player player : players) {
			if (player.getLocation().distanceSquared(location) > radius)
				continue;

			player.playEffect(location, Effect.EXTINGUISH, 0);
			SpawnUtils.makeParticles(player, location, new Vector(.1, .1, .1), 0, 30, "smoke");
		}
	}
}
