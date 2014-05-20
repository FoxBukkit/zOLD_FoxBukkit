/**
 * This file is part of YiffBukkit.
 *
 * YiffBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * YiffBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with YiffBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.doridian.yiffbukkit.teleportation.commands;

import de.doridian.yiffbukkit.core.util.PlayerHelper;
import de.doridian.yiffbukkit.main.PermissionDeniedException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;
import de.doridian.yiffbukkit.main.util.PlayerFindException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Names("send")
@Help("Teleports the specified source user to the specified target user.")
@Usage("<source> <target>")
@Permission("yiffbukkit.teleport.send")
public class SendCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr, String commandName) throws PlayerFindException, PermissionDeniedException {
		Player fromPlayer = playerHelper.matchPlayerSingle(args[0]);

		Player toPlayer = playerHelper.matchPlayerSingle(args[1]);

		if (!playerHelper.canSummon(commandSender, fromPlayer))
			throw new PermissionDeniedException();

		if (!playerHelper.canTp(commandSender, toPlayer))
			throw new PermissionDeniedException();

		plugin.playerHelper.teleportWithHistory(fromPlayer, toPlayer);

		PlayerHelper.sendServerMessage(commandSender.getName() + " sent " + fromPlayer.getName() + " to " + toPlayer.getName());
	}
}
