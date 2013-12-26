package de.doridian.yiffbukkit.main.util;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.chat.Parser;
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

	public RunString(String commandString, Set<String> filter) throws YiffBukkitCommandException {
		this(parseCommandString(commandString, filter));
	}

	private static List<String> parseCommandString(String commandString, Set<String> filter) throws YiffBukkitCommandException {
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
				throw new YiffBukkitCommandException("Command \u00a79"+commandName+"\u00a7f cannot be bound.");

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

			sb.append(Parser.escape(command));
		}

		return sb.toString();
	}

	public String getString() {
		return getString("; ");
	}

}
