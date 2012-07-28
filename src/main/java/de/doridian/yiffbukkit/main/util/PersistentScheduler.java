package de.doridian.yiffbukkit.main.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import de.doridian.yiffbukkit.main.StateContainer;
import de.doridian.yiffbukkitsplit.YiffBukkit;

public class PersistentScheduler extends StateContainer {
	private static PersistentScheduler instance;

	public PersistentScheduler() {
		instance = this;
	}

	public static void schedule(long t, CommandSender commandSender, RunString runnable) {
		instance.sched(t, commandSender, runnable);
	}

	private void sched(long t, CommandSender commandSender, RunString runnable) {
		queue.add(new Entry(System.currentTimeMillis() + t, commandSender.getName(), runnable));
		refresh();
	}

	private static class Entry implements Comparable<Entry> {
		private final long timestamp;

		private final String name;
		private final RunString parsedCommands;

		public Entry(long timestamp, String name, RunString parsedCommands) {
			this.timestamp = timestamp;
			this.name = name;
			this.parsedCommands = parsedCommands;
		}

		private static CommandSender getCommandSender(String name) {
			if (name.equals("CONSOLE"))
				return Bukkit.getConsoleSender();

			return Bukkit.getPlayerExact(name);
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
			parsedCommands.run(getCommandSender(name));
		}

		public Entry(Map<String, List<String>> section) {
			this(
					Long.parseLong(section.get("timestamp").get(0)),
					section.get("sender").get(0),
					new RunString(section.get("commands"))
			);
		}

		public Map<String, List<String>> save() {
			Map<String, List<String>> section = new TreeMap<String, List<String>>();

			section.put("timestamp", Arrays.asList(""+timestamp));
			section.put("sender", Arrays.asList(name));
			section.put("commands", parsedCommands.getCommands());

			return section;
		}
	}

	private long next = Long.MAX_VALUE;
	private final PriorityQueue<Entry> queue = new PriorityQueue<Entry>();

	{
		new ScheduledTask(YiffBukkit.instance) {
			@Override
			public void run() {
				if (System.currentTimeMillis() < next)
					return;

				final Entry entry = queue.poll();
				entry.run();

				refresh();
			}
		}.scheduleSyncRepeating(0, 1);
	}

	private void refresh() {
		if (queue.isEmpty())
			next = Long.MAX_VALUE;
		else
			next = queue.peek().timestamp;

		save();
	}

	@Saver("scheduler")
	public void save() {
		final List<Map<String, List<String>>> namesakes = new ArrayList<Map<String, List<String>>>();
		for (Entry entry : queue) {
			namesakes.add(entry.save());
		}
		Ini.save("scheduler.txt", Collections.singletonMap("entry", namesakes));
	}

	@Loader("scheduler")
	public void load() {
		queue.clear();
		Map<String, List<Map<String, List<String>>>> sections = Ini.load("scheduler.txt");

		final List<Map<String, List<String>>> namesakes = sections.get("entry");
		if (namesakes != null) {
			for (Map<String, List<String>> section : namesakes) {
				queue.add(new Entry(section));
			}
		}
		refresh();
	}
}
