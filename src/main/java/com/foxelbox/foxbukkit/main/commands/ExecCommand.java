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

import com.foxelbox.foxbukkit.core.FoxBukkit;
import com.foxelbox.foxbukkit.main.FoxBukkitCommandException;
import com.foxelbox.foxbukkit.main.commands.system.ICommand;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Help;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Names;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Permission;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Usage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Names("exec")
@Help("Enters all non-empty lines that dont start with # from the given file into your chat. Files are taken from scripts/<filename>.txt")
@Usage("<filename>")
@Permission("foxbukkit.exec")
public class ExecCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr, String commandName) throws FoxBukkitCommandException {
		if (argStr.isEmpty())
			throw new FoxBukkitCommandException("Expected file name.");

		if (argStr.matches("[^-a-zA-Z0-9_ ]"))
			throw new FoxBukkitCommandException("Invalid file name. Can only contain a-z, A-Z, 0-9, underscore(_), dash(-) and space");

		List<String> list = new ArrayList<>();

		try {
			BufferedReader stream = new BufferedReader(FoxBukkit.instance.configuration.makeReader("scripts/" + argStr + ".txt"));
			String line;
			while((line = stream.readLine()) != null) {
				if (line.isEmpty())
					continue;

				if (line.charAt(0) == '#')
					continue;

				if (line.charAt(0) == '\\')
					line = line.substring(1);

				list.add(line);
			}
		}
		catch (IOException e) {
			throw new FoxBukkitCommandException("Error while reading file.", e);
		}

		for (String line : list) {
			chat(commandSender, line);
		}
	}

	private void chat(CommandSender commandSender, String line) {
		if (commandSender instanceof Player) {
			((Player) commandSender).chat(line);
			return;
		}

		if (line.startsWith("/")) {
			Bukkit.getServer().dispatchCommand(commandSender, line.substring(1));
			return;
		}

		Bukkit.getServer().dispatchCommand(commandSender, "say "+line);
	}
}
