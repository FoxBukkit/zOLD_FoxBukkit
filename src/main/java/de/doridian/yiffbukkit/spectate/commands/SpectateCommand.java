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
package de.doridian.yiffbukkit.spectate.commands;

import de.doridian.yiffbukkit.core.YiffBukkit;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.spectate.SpectatePlayer;
import org.bukkit.entity.Player;

@ICommand.Names({"spectate","spec"})
@ICommand.Permission("yiffbukkit.spectate")
public class SpectateCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr, String commandName) throws YiffBukkitCommandException {
		SpectatePlayer currentPlayer = SpectatePlayer.wrapPlayer(ply);
		SpectatePlayer otherPlayer = SpectatePlayer.wrapPlayer(YiffBukkit.instance.playerHelper.matchPlayerSingle(argStr));
		currentPlayer.spectatePlayer(otherPlayer);
	}
}
