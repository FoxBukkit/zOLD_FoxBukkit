package de.doridian.yiffbukkit.commands;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import de.doridian.yiffbukkit.PermissionDeniedException;
import de.doridian.yiffbukkit.ToolBind;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;
import de.doridian.yiffbukkit.transmute.Shape;

@Names({"shapeaction", "sac"})
@Help(
		"Gives your current shape a command.\n" +
		"Flags:\n" +
		"  -e to issue the command to an entity (binds to a tool)\n" +
		"  -i <item name or id> together with -e to bind to a specific tool."
)
@Usage("[<flags>][<command>]")
@Permission("yiffbukkit.transmute.shapeaction")
@BooleanFlags("e")
@StringFlags("i")
public class ShapeActionCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		final String shapeAction = parseFlags(argStr);

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

			playerHelper.addToolMapping(ply, toolType, new ToolBind(shapeAction, ply) {
				@Override
				public void run(PlayerInteractEntityEvent event) throws YiffBukkitCommandException {
					final Player player = event.getPlayer();
					if (!plugin.permissionHandler.has(player, "yiffbukkit.transmute.others"))
						throw new PermissionDeniedException();

					final Entity entity = event.getRightClicked();

					final Shape shape = plugin.transmute.getShape(entity);
					if (shape == null)
						throw new YiffBukkitCommandException("Your target is not currently transmuted.");

					shape.runAction(shapeAction);
				}
			});

			playerHelper.sendDirectedMessage(ply, "Bound §9"+shapeAction+"§f to your tool (§e"+toolType.name()+"§f). Right-click an entity to use.");
			return;
		}
		final Shape shape = plugin.transmute.getShape(ply);
		if (shape == null)
			throw new YiffBukkitCommandException("You are not currently transmuted.");

		shape.runAction(shapeAction);
	}
}
