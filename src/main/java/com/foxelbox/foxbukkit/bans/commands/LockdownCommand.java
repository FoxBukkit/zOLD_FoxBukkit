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
package com.foxelbox.foxbukkit.bans.commands;

import com.foxelbox.foxbukkit.bans.LockDownMode;
import com.foxelbox.foxbukkit.core.util.PlayerHelper;
import com.foxelbox.foxbukkit.main.FoxBukkitCommandException;
import com.foxelbox.foxbukkit.main.commands.system.ICommand;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Help;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Names;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Permission;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Usage;
import org.bukkit.command.CommandSender;

import java.io.IOException;

@Names("lockdown")
@Help("Locks or unlocks the server for guests")
@Usage("[on|off]")
@Permission("foxbukkit.users.lockdown")
public class LockdownCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr, String commandName) throws FoxBukkitCommandException {
		final String name = commandSender.getName();
		argStr = argStr.toUpperCase();
		final LockDownMode newMode;
		if (argStr.isEmpty()) {
			newMode = plugin.bans.lockdownMode == LockDownMode.OFF ? LockDownMode.KICK : LockDownMode.OFF;
		}
		else if (argStr.equals("ON")){
			newMode = LockDownMode.KICK;
		}
		else {
			try {
				newMode = LockDownMode.valueOf(argStr);
			}
			catch (IllegalArgumentException e) {
				throw new FoxBukkitCommandException("Invalid lockdown mode.", e);
			}
		}

		if (newMode == plugin.bans.lockdownMode) {
			throw new FoxBukkitCommandException(newMode.getDescription());
		}

		plugin.bans.lockdownMode = newMode;

		if (plugin.bans.lockdownMode == LockDownMode.FIREWALL) {
			try {
				Runtime.getRuntime().exec("./wally F");
				System.out.println("Flushed blocked IPs.");
			} catch (IOException e) {
				System.out.println("Failed to flush blocked IPs.");
			}
		}

		if (newMode == LockDownMode.OFF) {
			PlayerHelper.sendServerMessage(name + " unlocked the server for guests.", 1);
		}
		else {
			PlayerHelper.sendServerMessage(name + " locked the server for guests.", 1);
		}

	}
}
