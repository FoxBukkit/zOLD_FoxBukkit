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
package de.doridian.foxbukkit.warp;

import de.doridian.foxbukkit.core.FoxBukkit;
import de.doridian.foxbukkit.main.StateContainer;
import de.doridian.foxbukkit.main.util.Ini;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Pattern;

public class WarpEngine extends StateContainer {
	// TODO: public Map<String, String> warpMRU = new Hashtable<>();

	private FoxBukkit plugin;
	private Map<String, WarpDescriptor> warps = new Hashtable<>();

	public WarpEngine(FoxBukkit plugin) {
		this.plugin = plugin;
	}

	@Loader({"warps", "warp"})
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

	@Saver({"warps", "warp"})
	public void SaveWarps() {
		Map<String, List<Map<String, List<String>>>> sections = new TreeMap<>();
		for (Entry<String, WarpDescriptor> entry : warps.entrySet()) {
			WarpDescriptor warp = entry.getValue();
			sections.put(warp.name, Arrays.asList(warp.save()));
		}

		Ini.save("warps.txt", sections);
	}

	public WarpDescriptor setWarp(UUID ownerName, String name, Location location) throws WarpException {
		if (!Pattern.matches("^[-A-Za-z0-9_]+$", name))
			throw new WarpException("Warp name invalid.");

		if (warps.containsKey(name.toLowerCase()))
			throw new WarpException("Warp already exists.");

		WarpDescriptor warp = new WarpDescriptor(plugin, ownerName, name, location);
		warps.put(name.toLowerCase(), warp);
		SaveWarps();

		return warp;
	}

	public WarpDescriptor removeWarp(CommandSender commandSender, String name) throws WarpException {
		WarpDescriptor warp = warps.get(name.toLowerCase());

		if (warp == null)
			throw new WarpException("Warp not found.");

		if (warp.checkAccess(commandSender) < 3)
			throw new WarpException("Permission denied.").setColor('4');

		warps.remove(name.toLowerCase());
		SaveWarps();

		return warp;
	}

	public WarpDescriptor getWarp(CommandSender commandSender, String name) throws WarpException {
		WarpDescriptor warp = warps.get(name.toLowerCase());

		if (warp == null)
			throw new WarpException("Warp '"+name+"' not found.");

		if (warp.checkAccess(commandSender) < 1)
			throw new WarpException("Permission denied.").setColor('4');

		return warp;
	}

	public Map<String, WarpDescriptor> getWarps() {
		return new Hashtable<>(warps);
	}
}
