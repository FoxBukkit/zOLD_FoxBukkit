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
package de.doridian.yiffbukkit.jail.commands;

import de.doridian.yiffbukkit.core.util.PlayerHelper;
import de.doridian.yiffbukkit.jail.JailComponent;
import de.doridian.yiffbukkit.jail.JailException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;
import de.doridian.yiffbukkit.main.util.PlayerFindException;
import org.bukkit.entity.Player;

@Names("jail")
@Help("Sends someone to a previously defined jail cell.")
@Usage("<name> [release]")
@Permission("yiffbukkit.jail.jail")
public class JailCommand extends ICommand {
	private final JailComponent jail = (JailComponent) plugin.componentSystem.getComponent("jail");

	@Override
	public void Run(Player ply, String[] args, String argStr, String commandName) throws PlayerFindException, JailException {
		if (args.length == 0) {
			PlayerHelper.sendDirectedMessage(ply, "Not enough arguments.");
			return;
		}

		Player otherply = playerHelper.matchPlayerSingle(args[0]);

		if (args.length == 1) {
			jail.engine.jailPlayer(otherply, true);
			PlayerHelper.sendServerMessage(ply.getName() + " sent " + otherply.getName() + " to jail.");
		}
		else if (args[1].equals("release") || args[1].equals("rel") || args[1].equals("r")) {
			jail.engine.jailPlayer(otherply, false);
			PlayerHelper.sendServerMessage(ply.getName() + " released " + otherply.getName() + " from jail.");
		}
		else {
			PlayerHelper.sendDirectedMessage(ply, "Invalid argument.");
		}
	}
}
