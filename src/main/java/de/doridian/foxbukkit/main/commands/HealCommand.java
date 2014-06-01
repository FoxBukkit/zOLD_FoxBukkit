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
package de.doridian.foxbukkit.main.commands;

import de.doridian.foxbukkit.core.util.PlayerHelper;
import de.doridian.foxbukkit.main.FoxBukkitCommandException;
import de.doridian.foxbukkit.main.commands.system.ICommand;
import de.doridian.foxbukkit.main.commands.system.ICommand.BooleanFlags;
import de.doridian.foxbukkit.main.commands.system.ICommand.Help;
import de.doridian.foxbukkit.main.commands.system.ICommand.Names;
import de.doridian.foxbukkit.main.commands.system.ICommand.Permission;
import de.doridian.foxbukkit.main.commands.system.ICommand.Usage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Names("heal")
@Help("Heals a player fully or by the given amount.")
@Usage("[<name>] [<amount>]")
@BooleanFlags("f")
@Permission("foxbukkit.players.heal")
public class HealCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr, String commandName) throws FoxBukkitCommandException {
		args = parseFlags(args);
		int amount;
		Player target;
		switch (args.length) {
		case 0:
			//heal <name> - heal yourself fully
			amount = 20;
			target = asPlayer(commandSender);

			break;

		case 1:
			try {
				//heal <amount> - heal yourself by the given amount
				amount = Integer.parseInt(args[0]);
				target = asPlayer(commandSender);
			}
			catch (NumberFormatException e) {
				//heal <name> - heal someone fully
				amount = 20;
				target = playerHelper.matchPlayerSingle(args[0]);
			}
			break;

		default:
			try {
				//heal <amount> <name> - heal someone by the given amount
				amount = Integer.parseInt(args[0]);
				target = playerHelper.matchPlayerSingle(args[1]);
			}
			catch (NumberFormatException e) {
				//heal <name> <...> - not sure yet
				target = playerHelper.matchPlayerSingle(args[0]);

				try {
					//heal <name> <amount> - heal someone by the given amount
					amount = Integer.parseInt(args[1]);
				}
				catch (NumberFormatException e2) {
					throw new FoxBukkitCommandException("Syntax error", e2);
				}
			}
			break;
		}

		if (booleanFlags.contains('f')) {
			target.setFoodLevel(Math.min(20, target.getFoodLevel() + amount));

			if (amount >= 20)
				PlayerHelper.sendServerMessage(commandSender.getName() + " fully fed " + target.getName() + ".");
			else
				PlayerHelper.sendServerMessage(commandSender.getName() + " fed " + target.getName() + " by " + amount + " points.");
		}
		else {
			target.setHealth(Math.min(20, target.getHealth() + amount));

			if (amount >= 20)
				PlayerHelper.sendServerMessage(commandSender.getName() + " fully healed " + target.getName() + ".");
			else
				PlayerHelper.sendServerMessage(commandSender.getName() + " healed " + target.getName() + " by " + amount + " points.");
		}
	}
}
