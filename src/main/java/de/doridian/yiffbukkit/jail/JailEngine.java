package de.doridian.yiffbukkit.jail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.util.Ini;

public class JailEngine {
	public class JailDescriptor {
		World world;
		Vector position, size;

		public JailDescriptor(World world, Vector pos1, Vector pos2) {
			this.world = world;
			position = pos1.clone().add(new Vector(0.5, 0, 0.5));
			size = pos2.clone().subtract(pos1);
			System.out.println(position);
			System.out.println(size);
		}

		public JailDescriptor(Map<String, List<String>> section) {
			load(section);
		}

		public void load(Map<String, List<String>> section) {
			world = Ini.loadWorld(section, "%s", plugin.getServer());
			position = Ini.loadVector(section, "position%s");
			size = Ini.loadVector(section, "size%s");
		}

		public Map<String, List<String>> save() {
			Map<String, List<String>> section = new TreeMap<String, List<String>>();

			Ini.saveWorld(section, "%s", world);
			Ini.saveVector(section, "position%s", position);
			Ini.saveVector(section, "size%s", size);

			return section;
		}

		public void jailPlayer(Player ply) {
			Vector vector = position.clone().add(size.clone().multiply(Math.random()));
			Location location = vector.toLocation(world);
			ply.teleportTo(location);
		}

		public Vector center() {
			return position.clone().add(size.clone().multiply(0.5));
		}

	}

	private List<JailDescriptor> jails = new ArrayList<JailDescriptor>();
	private Map<String, Location> inmates = new HashMap<String, Location>();
	private YiffBukkit plugin;

	public JailEngine(YiffBukkit plugin) {
		this.plugin = plugin;
		LoadJails();
	}

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

				Location location = Ini.loadLocation(namesakes.get(0), "prev%s", plugin.getServer());
				if (location == null)
					location = plugin.getServer().getWorlds().get(0).getSpawnLocation();

				inmates.put(playerName, location);
			}
			else {
				System.err.println("Invalid section in jails.txt.");
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void SaveJails() {
		Map<String, List<Map<String, List<String>>>> sections = new TreeMap<String, List<Map<String, List<String>>>>();
		List<Map<String, List<String>>> jailSections = new ArrayList<Map<String, List<String>>>();  
		for (JailDescriptor entry : jails) {
			jailSections.add(entry.save());
		}
		sections.put("jail", jailSections);

		for(Map.Entry<String, Location> entry : inmates.entrySet()) {
			Map<String, List<String>> section = new TreeMap<String, List<String>>();
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
				ply.teleportTo(previousLocation);
		}
		SaveJails();
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

			if (!jail.world.equals(world))
				continue;

			double distanceSquared = pos.distanceSquared(jail.center());
			if (distanceSquared >= minDistanceSquared)
				continue;

			minDistanceSquared = distanceSquared;
			closestIndex = i;
		}

		jails.remove(closestIndex);

		SaveJails();
	}
}
