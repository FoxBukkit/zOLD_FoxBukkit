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
package de.doridian.yiffbukkit.main.commands;

import de.doridian.yiffbukkit.core.YiffBukkit;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;
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
@Permission("yiffbukkit.exec")
public class ExecCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr, String commandName) throws YiffBukkitCommandException {
		if (argStr.isEmpty())
			throw new YiffBukkitCommandException("Expected file name.");

		if (argStr.matches("[^-a-zA-Z0-9_ ]"))
			throw new YiffBukkitCommandException("Invalid file name. Can only contain a-z, A-Z, 0-9, underscore(_), dash(-) and space");

		List<String> list = new ArrayList<>();

		try {
			BufferedReader stream = new BufferedReader(YiffBukkit.instance.configuration.makeReader("scripts/" + argStr + ".txt"));
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
			throw new YiffBukkitCommandException("Error while reading file.", e);
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
