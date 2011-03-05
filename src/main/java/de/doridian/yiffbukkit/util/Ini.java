package de.doridian.yiffbukkit.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	public static void save(String fileName, Map<String, ? extends Iterable<? extends Map<String, ? extends Iterable<String>>>> x) {
		try {
			BufferedWriter stream = new BufferedWriter(new FileWriter(fileName));
			for (Entry<String, ? extends Iterable<? extends Map<String, ? extends Iterable<String>>>> entry : x.entrySet()) {
				String sectionHeader = "["+entry.getKey()+"]";
				for (Map<String, ? extends Iterable<String>> section : entry.getValue()) {
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

	private static void saveSection(BufferedWriter stream, Map<String, ? extends Iterable<String>> section) throws IOException {
		for (Map.Entry<String, ? extends Iterable<String>> entry : section.entrySet()) {
			String key = entry.getKey();
			for (String value : entry.getValue()) {
				stream.write(key);
				stream.write("=");
				stream.write(value);
				stream.newLine();
			}
		}
	}
}
