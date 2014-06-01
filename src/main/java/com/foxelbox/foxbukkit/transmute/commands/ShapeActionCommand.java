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
package com.foxelbox.foxbukkit.transmute.commands;

import com.foxelbox.foxbukkit.core.util.PlayerHelper;
import com.foxelbox.foxbukkit.main.FoxBukkitCommandException;
import com.foxelbox.foxbukkit.main.PermissionDeniedException;
import com.foxelbox.foxbukkit.main.ToolBind;
import com.foxelbox.foxbukkit.main.commands.system.ICommand;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.BooleanFlags;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Help;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Names;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Permission;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.StringFlags;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Usage;
import com.foxelbox.foxbukkit.spawning.commands.GiveCommand;
import com.foxelbox.foxbukkit.transmute.Shape;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;

@Names({"shapeaction", "sac"})
@Help(
		"Gives your current shape a command.\n" +
		"Flags:\n" +
		"  -e to issue the command to an entity (binds to a tool)\n" +
		"  -i <item name or id> together with -e to bind to a specific tool\n" +
		"  -l to transmute the last entity you transmuted\n" +
		"  -x to bind to the left instead of the right mouse button"
)
@Usage("[<flags>][<command>]")
@Permission("foxbukkit.transmute.shapeaction")
@BooleanFlags("elx")
@StringFlags("i")
public class ShapeActionCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr, String commandName) throws FoxBukkitCommandException {
		final String shapeAction = parseFlags(argStr);

		if (booleanFlags.contains('e')) {
			if (!ply.hasPermission("foxbukkit.transmute.shapeaction.others"))
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

			ToolBind.add(ply, toolType, left, new ToolBind(shapeAction, ply) {
				@Override
				public boolean run(PlayerInteractEntityEvent event) throws FoxBukkitCommandException {
					final Player player = event.getPlayer();
					if (!player.hasPermission("foxbukkit.transmute.shapeaction.others"))
						throw new PermissionDeniedException();

					final Entity entity = event.getRightClicked();

					final Shape shape = plugin.transmute.getShape(entity);
					if (shape == null)
						throw new FoxBukkitCommandException("Your target is not currently transmuted.");

					shape.runAction(player, shapeAction);

					return true;
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
			throw new FoxBukkitCommandException("Not currently transmuted.");

		shape.runAction(ply, shapeAction);
	}
}
