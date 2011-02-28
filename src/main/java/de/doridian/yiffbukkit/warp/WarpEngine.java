package de.doridian.yiffbukkit.warp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;

import de.doridian.yiffbukkit.YiffBukkit;

public class WarpEngine {
	public Map<String, String> warpMRU = new Hashtable<String, String>(); // TODO!

	private YiffBukkit plugin;
	private Map<String, WarpDescriptor> warps = new Hashtable<String, WarpDescriptor>();

	public WarpEngine(YiffBukkit plugin) {
		this.plugin = plugin;
		LoadWarps();
	}

	public void LoadWarps() {
		Pattern sectionStartPattern = Pattern.compile("^\\[(.+)\\]$");

		try {
			BufferedReader stream = new BufferedReader(new FileReader("warps.txt"));
			String line;
			while((line = stream.readLine()) != null) {
				if (line.trim().isEmpty())
					continue;

				Matcher matcher = sectionStartPattern.matcher(line);

				if (!matcher.matches()) {
					System.err.println("Malformed line in warps.txt.");
					continue;
				}

				String warpName = matcher.group(1);
				warps.put(warpName.toLowerCase(), new WarpDescriptor(plugin, warpName, stream));
			}
			stream.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void SaveWarps() {
		try 
		{
			BufferedWriter stream = new BufferedWriter(new FileWriter("warps.txt"));
			for (Map.Entry<String, WarpDescriptor> entry : warps.entrySet()) {
				WarpDescriptor warp = entry.getValue();
				stream.write("["+warp.name+"]");
				stream.newLine();
				warp.save(stream);
				stream.newLine();
			}
			stream.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public WarpDescriptor setWarp(String ownerName, String name, Location location) throws WarpException {
		if (!Pattern.matches("^[A-Za-z0-9_]+$", name))
			throw new WarpException("Warp name invalid.");

		if (warps.containsKey(name.toLowerCase()))
			throw new WarpException("Warp already exists.");

		WarpDescriptor warp = new WarpDescriptor(plugin, ownerName, name, location);
		warps.put(name.toLowerCase(), warp);
		SaveWarps();

		return warp;
	}

	public WarpDescriptor removeWarp(String commandSenderName, String name) throws WarpException {
		WarpDescriptor warp = warps.get(name);

		if (warp == null)
			throw new WarpException("Warp not found.");

		warp.checkAccess(commandSenderName);

		warps.remove(name);
		SaveWarps();

		return warp;
	}

	public WarpDescriptor getWarp(String playerName, String name) throws WarpException {
		WarpDescriptor warp = warps.get(name);

		if (warp == null)
			throw new WarpException("Warp not found.");

		if (warp.checkAccess(playerName) >= 1)
			return warp;
		throw new WarpException("Permission denied.");
	}

	public Map<String, WarpDescriptor> getWarps() {
		return new Hashtable<String, WarpDescriptor>(warps);
	}
}
