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
package de.doridian.foxbukkit.teleportation.commands;

import de.doridian.foxbukkit.core.util.PlayerHelper;
import de.doridian.foxbukkit.main.FoxBukkitCommandException;
import de.doridian.foxbukkit.main.PermissionDeniedException;
import de.doridian.foxbukkit.main.commands.system.ICommand;
import de.doridian.foxbukkit.main.commands.system.ICommand.Help;
import de.doridian.foxbukkit.main.commands.system.ICommand.Names;
import de.doridian.foxbukkit.main.commands.system.ICommand.Permission;
import de.doridian.foxbukkit.main.commands.system.ICommand.Usage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Names({"summon", "tphere"})
@Help("Teleports the specified user to you")
@Usage("<name>")
@Permission("foxbukkit.teleport.summon")
public class SummonCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr, String commandName) throws FoxBukkitCommandException {
		Player otherply = playerHelper.matchPlayerSingle(args[0]);

		if (!playerHelper.canSummon(commandSender, otherply))
			throw new PermissionDeniedException();

		plugin.playerHelper.teleportWithHistory(otherply, getCommandSenderLocation(commandSender, false));

		PlayerHelper.sendServerMessage(commandSender.getName() + " summoned " + otherply.getName());
	}
}
