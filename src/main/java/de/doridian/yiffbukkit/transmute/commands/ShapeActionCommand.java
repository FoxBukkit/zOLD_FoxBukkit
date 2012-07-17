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
import de.doridian.yiffbukkit.transmute.Shape;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;

@Names({"shapeaction", "sac"})
@Help(
		"Gives your current shape a command.\n" +
		"Flags:\n" +
		"  -e to issue the command to an entity (binds to a tool)\n" +
		"  -i <item name or id> together with -e to bind to a specific tool.\n" +
		"  -l to transmute the last entity you transmuted"
)
@Usage("[<flags>][<command>]")
@Permission("yiffbukkit.transmute.shapeaction")
@BooleanFlags("el")
@StringFlags("i")
public class ShapeActionCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		final String shapeAction = parseFlags(argStr);

		if (booleanFlags.contains('e')) {
			if (!ply.hasPermission("yiffbukkit.transmute.shapeaction.others"))
				throw new PermissionDeniedException();

			final Material toolType;
			if (stringFlags.containsKey('i')) {
				final String materialName = stringFlags.get('i');
				toolType = GiveCommand.matchMaterial(materialName);
			}
			else {
				toolType = ply.getItemInHand().getType();
			}

			ToolBind.add(ply, toolType, new ToolBind(shapeAction, ply) {
				@Override
				public void run(PlayerInteractEntityEvent event) throws YiffBukkitCommandException {
					final Player player = event.getPlayer();
					if (!player.hasPermission("yiffbukkit.transmute.shapeaction.others"))
						throw new PermissionDeniedException();

					final Entity entity = event.getRightClicked();

					final Shape shape = plugin.transmute.getShape(entity);
					if (shape == null)
						throw new YiffBukkitCommandException("Your target is not currently transmuted.");

					shape.runAction(player, shapeAction);
				}
			});

			PlayerHelper.sendDirectedMessage(ply, "Bound \u00a79"+shapeAction+"\u00a7f to your tool (\u00a7e"+toolType.name()+"\u00a7f). Right-click an entity to use.");
			return;
		}

		final Entity target;
		if (booleanFlags.contains('l')) {
			target = plugin.transmute.getLastTransmutedEntity(ply);
		}
		else {
			target = ply;
		}

		final Shape shape = plugin.transmute.getShape(target);
		if (shape == null)
			throw new YiffBukkitCommandException("Not currently transmuted.");

		shape.runAction(ply, shapeAction);
	}
}
