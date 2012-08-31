package de.doridian.yiffbukkit.jail;

import de.doridian.yiffbukkit.jail.listeners.JailPlayerListener;
import de.doridian.yiffbukkit.main.StateContainer;
import de.doridian.yiffbukkit.main.util.Ini;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;

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
	public static class JailDescriptor {
		World world;
		Vector position;
		double sizeX, sizeZ;

		public JailDescriptor(World world, Vector pos1, Vector pos2) {
			this.world = world;
			position = pos1.clone().add(new Vector(0.5, 0, 0.5));
			Vector size = pos2.clone().subtract(pos1);
			sizeX = size.getX();
			sizeZ = size.getZ();
		}

		public JailDescriptor(Map<String, List<String>> section) {
			load(section);
		}

		public void load(Map<String, List<String>> section) {
			world = Ini.loadWorld(section, "%s");
			position = Ini.loadVector(section, "position%s");
			Vector size = Ini.loadVector(section, "size%s");
			sizeX = size.getX();
			sizeZ = size.getZ();
		}

		public Map<String, List<String>> save() {
			Map<String, List<String>> section = new TreeMap<String, List<String>>();

			Ini.saveWorld(section, "%s", world);
			Ini.saveVector(section, "position%s", position);
			Ini.saveVector(section, "size%s", new Vector(sizeX, 0, sizeZ));

			return section;
		}

		public void jailPlayer(Player ply) {
			Vector vector = position.clone().add(new Vector(sizeX*Math.random(), 0, sizeZ*Math.random()));
			Location location = vector.toLocation(world);
			ply.teleport(location);
		}

		public Vector center() {
			return position.clone().add(new Vector(sizeX*0.5, 0, sizeZ*0.5));
		}
	}

	private List<JailDescriptor> jails = new ArrayList<JailDescriptor>();
	private Map<String, Location> inmates = new HashMap<String, Location>();
	public YiffBukkit plugin;

	public JailEngine(YiffBukkit plugin) {
		this.plugin = plugin;

		new JailPlayerListener(this);
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
		Map<String, List<Map<String, List<String>>>> sections = new TreeMap<String, List<Map<String, List<String>>>>();
		List<Map<String, List<String>>> jailSections = new ArrayList<Map<String, List<String>>>();  
		for (JailDescriptor entry : jails) {
			jailSections.add(entry.save());
		}
		sections.put("jail", jailSections);

		for(Map.Entry<String, Location> entry : inmates.entrySet()) {
			Map<String, List<String>> section = new TreeMap<String, List<String>>();
			Ini.saveLocation(section, "prev%s", entry.getValue());

			@SuppressWarnings("unchecked")
			final List<Map<String, List<String>>> wrappedSection = Arrays.asList(section);
			sections.put("inmate "+entry.getKey(), wrappedSection);
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
