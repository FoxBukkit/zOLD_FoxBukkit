package de.doridian.yiffbukkit.main.commands;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.command.CommandSender;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.ICommand.*;
import de.doridian.yiffbukkit.main.util.RunString;
import de.doridian.yiffbukkit.main.util.ScheduledTask;

@Names("at")
@Help("Runs a command after the given amount of seconds.")
@Usage("<seconds> <command>[;<command>[;<command> ...]")
@Permission("yiffbukkit.at")
public class AtCommand extends ICommand {
	final Pattern argumentPattern = Pattern.compile("^([^ ]+) (.*)$");
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		Matcher matcher = argumentPattern.matcher(argStr);
		if (!matcher.matches())
			throw new YiffBukkitCommandException("Syntax error");

		long t = (long) (Double.parseDouble(matcher.group(1))*1000);
		String commandString = matcher.group(2);

		final RunString parsedCommands = new RunString(commandString , Collections.singleton(""));
		final Entry entry = new Entry(System.currentTimeMillis() + t, commandSender, parsedCommands);
		queue.add(entry);
		refreshNext();
	}

	public class Entry implements Comparable<Entry> {
		private final long timestamp;

		private final CommandSender commandSender;
		private final RunString commands;

		public Entry(long timestamp, CommandSender commandSender, RunString commands) {
			this.timestamp = timestamp;
			this.commandSender = commandSender;
			this.commands = commands;
		}

		@Override
		public int compareTo(Entry other) {
			if (this.timestamp < other.timestamp)
				return -1;

			if (this.timestamp > other.timestamp)
				return 1;

			return 0;
		}

		public void run() {
			commands.run(commandSender);
		}
	}

	private long next = Long.MAX_VALUE;
	protected PriorityQueue<Entry> queue = new PriorityQueue<Entry>();

	{
		new ScheduledTask(plugin) {
			@Override
			public void run() {
				if (System.currentTimeMillis() < next)
					return;

				final Entry entry = queue.poll();
				entry.run();

				refreshNext();
			}
		}.scheduleSyncRepeating(0, 1);
	}

	private void refreshNext() {
		if (queue.isEmpty())
			next = Long.MAX_VALUE;
		else
			next = queue.peek().timestamp;
	}
}
