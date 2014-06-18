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
package com.foxelbox.foxbukkit.main.util;

import com.foxelbox.foxbukkit.chat.HTMLParser;
import com.foxelbox.foxbukkit.main.FoxBukkitCommandException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RunString {
	private static final Pattern COMMAND_PATTERN = Pattern.compile("^([^ ]+).*$");
	private final List<String> commands;

	public RunString(List<String> commands) {
		this.commands = commands;
	}

	public RunString(String commandString, Set<String> filter) throws FoxBukkitCommandException {
		this(parseCommandString(commandString, filter));
	}

	private static List<String> parseCommandString(String commandString, Set<String> filter) throws FoxBukkitCommandException {
		List<String> commands = new ArrayList<>();

		for (String command : commandString.split(";")) {
			command = command.trim();
			if (command.charAt(0) == '/')
				command = command.substring(1);

			final Matcher commandMatcher = COMMAND_PATTERN.matcher(command);

			if (!commandMatcher.matches())
				continue;

			final String commandName = commandMatcher.group(1);
			if (filter.contains(commandName))
				throw new FoxBukkitCommandException("Command \u00a79"+commandName+"\u00a7f cannot be bound.");

			commands.add(command);
		}
		return commands;
	}

	public List<String> getCommands() {
		return commands;
	}

	public void run(CommandSender commandSender) {
		if (commandSender instanceof Player) {
			for (String command : commands) {
				((Player) commandSender).chat("/"+command);
			}
			return;
		}

		for (String command : commands) {
			Bukkit.getServer().dispatchCommand(commandSender, command);
		}
	}

	public String getString(String delimiter) {
		boolean first = true;
		final StringBuilder sb = new StringBuilder();
		for (String command : commands) {

			if (!first)
				sb.append(delimiter);
			first = false;

			sb.append(HTMLParser.escape(command));
		}

		return sb.toString();
	}

	public String getString() {
		return getString("; ");
	}

}
