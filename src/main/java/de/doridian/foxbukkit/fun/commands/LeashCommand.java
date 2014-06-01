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
package de.doridian.foxbukkit.fun.commands;

import de.doridian.foxbukkit.core.util.PlayerHelper;
import de.doridian.foxbukkit.main.FoxBukkitCommandException;
import de.doridian.foxbukkit.main.PermissionDeniedException;
import de.doridian.foxbukkit.main.commands.system.ICommand;
import de.doridian.foxbukkit.main.commands.system.ICommand.Help;
import de.doridian.foxbukkit.main.commands.system.ICommand.Names;
import de.doridian.foxbukkit.main.commands.system.ICommand.Permission;
import de.doridian.foxbukkit.main.commands.system.ICommand.Usage;
import org.bukkit.entity.Player;

@Names("leash")
@Help("Leashes or unleashes a player.")
@Usage("<name>")
@Permission("foxbukkit.players.leash")
public class LeashCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr, String commandName) throws FoxBukkitCommandException {
		if (args.length < 1)
			throw new FoxBukkitCommandException("Not enough arguments");

		Player otherply = playerHelper.matchPlayerSingle(args[0]);

		if (!playerHelper.canSummon(ply, otherply))
			throw new PermissionDeniedException();

		if (playerHelper.toggleLeash(ply, otherply))
			PlayerHelper.sendServerMessage(ply.getName() + " leashed " + otherply.getName());
		else
			PlayerHelper.sendServerMessage(ply.getName() + " unleashed " + otherply.getName());
	}
}
