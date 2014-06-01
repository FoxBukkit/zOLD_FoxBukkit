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
package de.doridian.foxbukkit.foxpoints.commands;

import de.doridian.foxbukkit.bans.FishBansResolver;
import de.doridian.foxbukkit.core.util.PlayerHelper;
import de.doridian.foxbukkit.main.FoxBukkitCommandException;
import de.doridian.foxbukkit.main.PermissionDeniedException;
import de.doridian.foxbukkit.main.commands.system.ICommand;
import de.doridian.foxbukkit.main.commands.system.ICommand.Help;
import de.doridian.foxbukkit.main.commands.system.ICommand.Names;
import de.doridian.foxbukkit.main.commands.system.ICommand.Permission;
import de.doridian.foxbukkit.main.commands.system.ICommand.Usage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@Names("addfunds")
@Help("Adds funds to the specified player's balance.")
@Usage("<exact player name> <cents/foxpoints to add>")
@Permission("foxbukkit.foxpoints.addfunds")
public class AddFundsComand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr, String commandName) throws FoxBukkitCommandException {
		if (commandSender instanceof Player)
			throw new PermissionDeniedException();

		if (args.length < 2)
			throw new FoxBukkitCommandException("Not enough arguments");

		final UUID playerName = FishBansResolver.getUUID(args[0]);
		final double amount = Double.parseDouble(args[1]);
		plugin.bank.addFunds(playerName, amount);

		PlayerHelper.sendDirectedMessage(commandSender, "Added "+amount+" YP to the account of "+playerName+".");

		// Notify the player if they're online
		final Player player = Bukkit.getServer().getPlayer(playerName);
		if (player != null) {
			double total = plugin.bank.getBalance(playerName);
			PlayerHelper.sendDirectedMessage(player, "Your balance was increased by "+amount+" YP. You now have "+total +" YP.");
		}
	}
}
