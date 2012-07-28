package de.doridian.yiffbukkit.main.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;

public class RunString {
	private static final Pattern commandPattern = Pattern.compile("^([^ ]+).*$");
	private final List<String> commands;
	private final String cleanString;
	public RunString(String commandString, Set<String> filter) throws YiffBukkitCommandException {
		commands = new ArrayList<String>();
		boolean first = true;
		StringBuilder sb = new StringBuilder();
		for (String command : commandString.split(";")) {
			command = command.trim();
			if (command.charAt(0) == '/')
				command = command.substring(1);

			Matcher commandMatcher = commandPattern.matcher(command);

			if (!commandMatcher.matches())
				continue;

			if (filter.contains(commandMatcher.group(1)))
				throw new YiffBukkitCommandException("Command \u00a79"+commandMatcher.group(1)+"\u00a7f cannot be bound.");

			getCommands().add(command);

			if (!first)
				sb.append("\u00a7c; \u00a79");
			first = false;

			sb.append(command);
		}
		cleanString = sb.toString();
	}

	public String getCleanString() {
		return cleanString;
	}

	public List<String> getCommands() {
		return commands;
	}

	public void run(CommandSender commandSender) {
		for (String command : commands) {
			Bukkit.getServer().dispatchCommand(commandSender, command);
		}
	}
}
