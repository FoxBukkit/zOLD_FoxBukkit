/**
 * This file is part of YiffBukkit.
 *
 * YiffBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * YiffBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with YiffBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.doridian.yiffbukkit.jail;

import de.doridian.yiffbukkit.core.YiffBukkit;
import de.doridian.yiffbukkit.core.util.PlayerHelper;
import de.doridian.yiffbukkit.main.StateContainer;
import de.doridian.yiffbukkit.main.util.Ini;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class JailEngine extends StateContainer {
	private List<JailDescriptor> jails = new ArrayList<>();
	private Map<String, Location> inmates = new HashMap<>();
	public YiffBukkit plugin;

	public JailEngine(YiffBukkit plugin) {
		this.plugin = plugin;
	}

	@Loader({"jails", "jail"})
	public void LoadJails() {
		jails.clear();
		inmates.clear();

		Map<String, List<Map<String, List<String>>>> sections = Ini.load("jails.txt");
		if (sections == null)
			return;

		for (Map.Entry<String, List<Map<String, List<String>>>> entry : sections.entrySet()) {
			String sectionName = entry.getKey();
			List<Map<String, List<String>>> namesakes = entry.getValue();

			if (sectionName.equals("jail")) {
				for (Map<String, List<String>> section : namesakes) {
					jails.add(new JailDescriptor(section));
				}
			}
			else if (namesakes.size() == 1 && sectionName.startsWith("inmate ")) {
				String playerName = sectionName.substring(7);

				Location location = Ini.loadLocation(namesakes.get(0), "prev%s");
				if (location == null)
					location = plugin.getServer().getWorlds().get(0).getSpawnLocation();

				inmates.put(playerName, location);
			}
			else {
				System.err.println("Invalid section in jails.txt.");
			}
		}
	}

	@Saver({"jails", "jail"})
	public void SaveJails() {
		Map<String, List<Map<String, List<String>>>> sections = new TreeMap<>();
		List<Map<String, List<String>>> jailSections = new ArrayList<>();
		for (JailDescriptor entry : jails) {
			jailSections.add(entry.save());
		}
		sections.put("jail", jailSections);

		for(Map.Entry<String, Location> entry : inmates.entrySet()) {
			Map<String, List<String>> section = new TreeMap<>();
			Ini.saveLocation(section, "prev%s", entry.getValue());

			sections.put("inmate "+entry.getKey(), Arrays.asList(section));
		}

		Ini.save("jails.txt", sections);
	}

	public boolean isJailed(Player ply) {
		return inmates.containsKey(ply.getName());
	}

	public void jailPlayer(Player ply, boolean jailed) throws JailException {
		String playerName = ply.getName();
		if (jailed) {
			if (jails.isEmpty())
				throw new JailException("No jails defined!");

			int index = (int) Math.floor(Math.random() * jails.size());

			if (inmates.containsKey(playerName))
				throw new JailException("Player is already jailed!");

			inmates.put(playerName, ply.getLocation());
			jails.get(index).jailPlayer(ply);
		}
		else {
			if (!inmates.containsKey(playerName))
				throw new JailException("Player is not jailed!");

			Location previousLocation = inmates.remove(playerName);
			if (previousLocation != null)
				ply.teleport(previousLocation);
		}
		SaveJails();
	}

	public void rejailPlayer(Player ply) {
		try {
			jailPlayer(ply, false);
			jailPlayer(ply, true);
			PlayerHelper.sendDirectedMessage(ply, "You are still jailed!");
		}
		catch (JailException e) { }
	}

	public void setJail(World world, Vector pos1, Vector pos2) {
		jails.add(new JailDescriptor(world, pos1, pos2));

		SaveJails();
	}

	public void removeJail(Location location) {
		if (jails.isEmpty())
			return;

		World world = location.getWorld();
		Vector pos = location.toVector();
		int closestIndex = -1;
		double minDistanceSquared = Double.MAX_VALUE; 

		for (int i = 0; i < jails.size(); ++i) {
			JailDescriptor jail = jails.get(i);

			if (!jail.getWorld().equals(world))
				continue;

			double distanceSquared = pos.distanceSquared(jail.getCenter());
			if (distanceSquared >= minDistanceSquared)
				continue;

			minDistanceSquared = distanceSquared;
			closestIndex = i;
		}

		jails.remove(closestIndex);

		SaveJails();
	}
}
