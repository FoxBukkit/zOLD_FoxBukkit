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
package com.foxelbox.foxbukkit.fun.commands;

import com.foxelbox.foxbukkit.core.util.MessageHelper;
import com.foxelbox.foxbukkit.main.FoxBukkitCommandException;
import com.foxelbox.foxbukkit.main.commands.system.ICommand;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.BooleanFlags;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Help;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Names;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Permission;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Usage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@Names("clear")
@Help("Clears your inventory or another player's. Use -a to include the toolbar.")
@Usage("[-a] [<name>]")
@BooleanFlags("a")
@Permission("foxbukkit.players.clear")
public class ClearCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr, String commandName) throws FoxBukkitCommandException {
		args = parseFlags(args);
		Player target;
		switch (args.length){
		case 0:
			//clear - clear inventory for self
			target = asPlayer(commandSender);
			break;

		case 1:
			//clear <name> - clear inventory of target
			target = playerHelper.matchPlayerSingle(args[0]);
			break;

		default:
			throw new FoxBukkitCommandException("Syntax error");
		}

		Inventory inventory = target.getInventory();

		final int startIndex = booleanFlags.contains('a') ? 0 : 9;
		
		for (int i = startIndex; i < 36; i++) {
			inventory.setItem(i, null);
		}

		MessageHelper.sendServerMessage(String.format("%1$s cleared %2$s's inventory.", MessageHelper.format(commandSender), MessageHelper.format(target)));
	}
}
