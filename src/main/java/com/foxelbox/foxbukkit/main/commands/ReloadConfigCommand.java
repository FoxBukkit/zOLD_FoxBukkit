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
package com.foxelbox.foxbukkit.main.commands;

import com.foxelbox.foxbukkit.core.util.PlayerHelper;
import com.foxelbox.foxbukkit.main.FoxBukkitCommandException;
import com.foxelbox.foxbukkit.main.StateContainer;
import com.foxelbox.foxbukkit.main.commands.system.ICommand;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.AbusePotential;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Help;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Names;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Permission;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Usage;
import org.bukkit.command.CommandSender;

@Names("reloadconf")
@Help("Reloads a named config.")
@Usage("")
@Permission("foxbukkit.reload")
@AbusePotential
public class ReloadConfigCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr, String commandName) throws FoxBukkitCommandException {
		final boolean success;
		try {
			success = StateContainer.loadSingle(argStr);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new FoxBukkitCommandException("Exception caught while loading config. See Log.", e);
		}

		if (!success)
			throw new FoxBukkitCommandException("Config not found");

		PlayerHelper.sendDirectedMessage(commandSender, "Reloaded "+argStr+" config.");
	}
}
