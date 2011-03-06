package de.doridian.yiffbukkit.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.util.Vector;

public abstract class Ini {
	public static Map<String, List<Map<String, List<String>>>> load(String fileName) {
		Pattern sectionStartPattern = Pattern.compile("^\\[(.+)\\]$");

		Map<String, List<Map<String, List<String>>>> sections = new TreeMap<String, List<Map<String, List<String>>>>();

		try {
			BufferedReader stream = new BufferedReader(new FileReader(fileName));
			String line;
			while((line = stream.readLine()) != null) {
				if (line.trim().isEmpty())
					continue;

				Matcher matcher = sectionStartPattern.matcher(line);

				if (!matcher.matches()) {
					System.err.println("Malformed line in "+fileName+".");
					continue;
				}

				String sectionName = matcher.group(1);

				List<Map<String, List<String>>> namesakes = sections.get(sectionName);

				if (namesakes == null)
					sections.put(sectionName, namesakes = new ArrayList<Map<String,List<String>>>());

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
		Map<String, List<String>> section = new TreeMap<String, List<String>>();

		Pattern linePattern = Pattern.compile("^([^=]+)=(.*)$");
		String line;
		while((line = stream.readLine()) != null) {
			if (line.trim().isEmpty())
				break;

			Matcher matcher = linePattern.matcher(line);

			if (!matcher.matches()) {
				System.err.println("Malformed line in file.");
				continue;
			}

			String key = matcher.group(1);
			String value = matcher.group(2);

			List<String> values = section.get(key);

			if (values == null)
				section.put(key, values = new ArrayList<String>());

			values.add(value);
		}
		return section;
	}

	public static void save(String fileName, Map<String, List<Map<String, List<String>>>> sections) {
		try {
			BufferedWriter stream = new BufferedWriter(new FileWriter(fileName));
			for (Map.Entry<String, List<Map<String, List<String>>>> entry : sections.entrySet()) {
				String sectionHeader = "["+entry.getKey()+"]";
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
			String key = entry.getKey();
			for (String value : entry.getValue()) {
				stream.write(key);
				stream.write("=");
				stream.write(value);
				stream.newLine();
			}
		}
	}


	public static World loadWorld(Map<String, List<String>> section, String format, Server server) {
		return server.getWorld(section.get(String.format(format, "world")).get(0));
	}

	public static Vector loadVector(Map<String, List<String>> section, String format) {
		return new Vector(
				Double.valueOf(section.get(String.format(format, "x")).get(0)),
				Double.valueOf(section.get(String.format(format, "y")).get(0)),
				Double.valueOf(section.get(String.format(format, "z")).get(0))
		);
	}

	public static Location loadLocation(Map<String, List<String>> section, String format, Server server) {
		try {
			return loadVector(section, format).toLocation(
					loadWorld(section, format, server),
					Float.valueOf(section.get(String.format(format, "yaw")).get(0)),
					Float.valueOf(section.get(String.format(format, "pitch")).get(0))
			);
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	public static void saveWorld(Map<String, List<String>> section, String string, World world) {
		section.put("world", Arrays.asList(world.getName()));
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
