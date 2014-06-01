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

import com.foxelbox.foxbukkit.core.FoxBukkit;
import com.foxelbox.foxbukkit.main.StateContainer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

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
			final CommandSender commandSender = getCommandSender(name);
			if (commandSender == null)
				return;

			parsedCommands.run(commandSender);
		}

		public Entry(Map<String, List<String>> section) {
			this(
					Long.parseLong(section.get("timestamp").get(0)),
					section.get("sender").get(0),
					new RunString(section.get("commands"))
			);
		}

		public Map<String, List<String>> save() {
			Map<String, List<String>> section = new TreeMap<>();

			section.put("timestamp", Arrays.asList(""+timestamp));
			section.put("sender", Arrays.asList(name));
			section.put("commands", parsedCommands.getCommands());

			return section;
		}
	}

	private long next = Long.MAX_VALUE;
	private final PriorityQueue<Entry> queue = new PriorityQueue<>();

	{
		new ScheduledTask(FoxBukkit.instance) {
			@Override
			public void run() {
				if (System.currentTimeMillis() < next)
					return;

				final Entry entry = queue.poll();
				try {
					entry.run();
				}
				finally {
					refresh();
				}
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
		final List<Map<String, List<String>>> namesakes = new ArrayList<>();
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
