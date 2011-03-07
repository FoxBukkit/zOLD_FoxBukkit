package de.doridian.yiffbukkit.warp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.bukkit.Location;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.util.Ini;

public class WarpEngine {
	public Map<String, String> warpMRU = new Hashtable<String, String>(); // TODO!

	private YiffBukkit plugin;
	private Map<String, WarpDescriptor> warps = new Hashtable<String, WarpDescriptor>();

	public WarpEngine(YiffBukkit plugin) {
		this.plugin = plugin;
		LoadWarps();
	}

	public void LoadWarps() {
		warps.clear();

		Map<String, List<Map<String, List<String>>>> sections = Ini.load("warps.txt");
		if (sections == null)
			return;

		for (Entry<String, List<Map<String, List<String>>>> entry : sections.entrySet()) {
			String warpName = entry.getKey();
			List<Map<String, List<String>>> namesakes = entry.getValue();

			warps.put(warpName.toLowerCase(), new WarpDescriptor(plugin, warpName, namesakes.get(0)));

			if (namesakes.size() > 1) {
				System.err.println("Duplicate section in warps.txt.");
			}
		}
	}

	public void SaveWarps() {
		try {
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
		WarpDescriptor warp = warps.get(name.toLowerCase());

		if (warp == null)
			throw new WarpException("Warp not found.");

		warp.checkAccess(commandSenderName);

		warps.remove(name.toLowerCase());
		SaveWarps();

		return warp;
	}

	public WarpDescriptor getWarp(String playerName, String name) throws WarpException {
		WarpDescriptor warp = warps.get(name.toLowerCase());

		if (warp == null)
			throw new WarpException("Warp not found.");

		if (warp.checkAccess(playerName) < 1)
			throw new WarpException("Permission denied.");

		return warp;
	}

	public Map<String, WarpDescriptor> getWarps() {
		return new Hashtable<String, WarpDescriptor>(warps);
	}
}
