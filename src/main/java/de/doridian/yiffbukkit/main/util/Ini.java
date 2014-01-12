package de.doridian.yiffbukkit.main.util;

import de.doridian.yiffbukkit.main.config.ConfigFileReader;
import de.doridian.yiffbukkit.main.config.ConfigFileWriter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Ini {
	private static final Pattern SECTION_START_PATTERN = Pattern.compile("^\\[(.+)\\]$");
	private static final Pattern LINE_PATTERN = Pattern.compile("^([^=]+)=(.*)$");

	public static Map<String, List<Map<String, List<String>>>> load(String fileName) {
		final Map<String, List<Map<String, List<String>>>> sections = new TreeMap<>();

		try {
			final BufferedReader stream = new BufferedReader(new ConfigFileReader(fileName));
			while (true) {
				final String line = stream.readLine();
				if (line == null)
					break;

				if (line.trim().isEmpty())
					continue;

				final Matcher matcher = SECTION_START_PATTERN.matcher(line);

				if (!matcher.matches()) {
					System.err.println("Malformed line in "+fileName+".");
					continue;
				}

				final String sectionName = matcher.group(1);

				List<Map<String, List<String>>> namesakes = sections.get(sectionName);

				if (namesakes == null)
					sections.put(sectionName, namesakes = new ArrayList<>());

				namesakes.add(loadSection(stream));
			}
			stream.close();
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return sections;
	}

	private static Map<String, List<String>> loadSection(BufferedReader stream) throws IOException {
		final Map<String, List<String>> section = new TreeMap<>();

		while (true) {
			final String line = stream.readLine();
			if (line == null)
				break;

			if (line.trim().isEmpty())
				break;

			final Matcher matcher = LINE_PATTERN.matcher(line);

			if (!matcher.matches()) {
				System.err.println("Malformed line in file.");
				continue;
			}

			final String key = matcher.group(1);
			final String value = matcher.group(2);

			List<String> values = section.get(key);

			if (values == null)
				section.put(key, values = new ArrayList<>());

			values.add(value);
		}
		return section;
	}

	public static void save(String fileName, Map<String, List<Map<String, List<String>>>> sections) {
		try {
			final BufferedWriter stream = new BufferedWriter(new ConfigFileWriter(fileName));
			for (Map.Entry<String, List<Map<String, List<String>>>> entry : sections.entrySet()) {
				final String sectionHeader = "["+entry.getKey()+"]";
				for (Map<String, List<String>> section : entry.getValue()) {
					stream.write(sectionHeader);
					stream.newLine();
					saveSection(stream, section);
					stream.newLine();
				}
			}
			stream.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void saveSection(BufferedWriter stream, Map<String, List<String>> section) throws IOException {
		for (Map.Entry<String, List<String>> entry : section.entrySet()) {
			final String key = entry.getKey();
			for (String value : entry.getValue()) {
				stream.write(key);
				stream.write("=");
				stream.write(value);
				stream.newLine();
			}
		}
	}


	public static World loadWorld(Map<String, List<String>> section, String format) {
		return Bukkit.getServer().getWorld(section.get(String.format(format, "world")).get(0));
	}

	public static Vector loadVector(Map<String, List<String>> section, String format) {
		return new Vector(
				Double.valueOf(section.get(String.format(format, "x")).get(0)),
				Double.valueOf(section.get(String.format(format, "y")).get(0)),
				Double.valueOf(section.get(String.format(format, "z")).get(0))
		);
	}

	public static Location loadLocation(Map<String, List<String>> section, String format) {
		try {
			return loadVector(section, format).toLocation(
					loadWorld(section, format),
					Float.valueOf(section.get(String.format(format, "yaw")).get(0)),
					Float.valueOf(section.get(String.format(format, "pitch")).get(0))
			);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	public static void saveWorld(Map<String, List<String>> section, String format, World world) {
		section.put(String.format(format, "world"), Arrays.asList(world.getName()));
	}

	public static void saveVector(Map<String, List<String>> section, String format, Vector vector) {
		section.put(String.format(format, "x"), Arrays.asList(String.valueOf(vector.getX())));
		section.put(String.format(format, "y"), Arrays.asList(String.valueOf(vector.getY())));
		section.put(String.format(format, "z"), Arrays.asList(String.valueOf(vector.getZ())));
	}

	public static void saveLocation(Map<String, List<String>> section, String format, Location location) {
		saveWorld(section, format, location.getWorld());
		saveVector(section, format, location.toVector());
		section.put(String.format(format, "yaw"), Arrays.asList(String.valueOf(location.getYaw())));
		section.put(String.format(format, "pitch"), Arrays.asList(String.valueOf(location.getPitch())));
	}
}
