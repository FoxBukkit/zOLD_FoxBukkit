package de.doridian.yiffbukkit.warp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.Location;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.util.Ini;

public class WarpDescriptor {
	private YiffBukkit plugin;
	private String ownerName;
	public String name;
	public Location location;
	public boolean isPublic = false;
	/* ranks:
	 * 0 = not allowed
	 * 1 = guest, can port to warp
	 * 2 = op, can set guest permissions
	 * 3 = admin/owner, can remove warp
	 */
	private Map<String, Integer> ranks = new HashMap<String, Integer>();

	public WarpDescriptor(YiffBukkit plugin, String ownerName, String name, Location location) {
		this.plugin = plugin;
		this.ownerName = ownerName;
		this.name = name;
		this.location = location.clone();
	}

	public WarpDescriptor(YiffBukkit plugin, String name, Map<String, List<String>> section) {
		this.plugin = plugin;
		this.name = name;
		load(section);
	}

	public int checkAccess(String playerName) {
		if (playerName == null)
			return 1;
		
		if (playerName.equals(ownerName))
			return 3;

		int playerLevel = plugin.playerHelper.getPlayerLevel(playerName);
		int ownerLevel = plugin.playerHelper.getPlayerLevel(ownerName);

		if (playerLevel > ownerLevel && plugin.permissionHandler.has(location.getWorld().getName(), playerName, "yiffbukkit.warp.override"))
			return 3;

		if (ranks.containsKey(playerName))
			return ranks.get(playerName);

		if (isPublic && playerLevel >= 1)
			return 1;

		return 0;
	}

	public void setAccess(String commandSenderName, String playerName, int rank) throws WarpException {
		int commandSenderRank = checkAccess(commandSenderName);
		int playerRank = checkAccess(playerName);

		if (commandSenderRank <= playerRank)
			throw new WarpException("Permission denied: You do not exceed the target's rank!").setColor('4');

		if (commandSenderRank <= rank)
			throw new WarpException("Permission denied: You do not exceed the specified rank!").setColor('4');

		if (rank == 0) {
			ranks.remove(playerName);
		}
		else {
			ranks.put(playerName, rank);
		}
	}

	public void setOwner(String commandSenderName, String newOwnerName) throws WarpException {
		if (checkAccess(commandSenderName) < 3)
			throw new WarpException("Permission denied: You do not own this warp!").setColor('4');

		ownerName = newOwnerName;
	}

	public String getOwner() {
		return ownerName;
	}

	public Map<String, Integer> getRanks() {
		return new Hashtable<String, Integer>(ranks);
	}

	public Map<String, List<String>> save() {
		Map<String, List<String>> section = new TreeMap<String, List<String>>();

		section.put("owner", Arrays.asList(ownerName));

		Ini.saveLocation(section, "%s", location);
		section.put("public", Arrays.asList(String.valueOf(isPublic)));

		List<String> ops = new ArrayList<String>();
		List<String> guests = new ArrayList<String>();
		for (Map.Entry<String, Integer> entry : ranks.entrySet()) {
			int rank = entry.getValue();
			if (rank == 1)
				guests.add(entry.getKey());
			else if (rank == 2)
				ops.add(entry.getKey());
			else {
				System.err.println("Invalid warp rank.");
				continue;
			}
		}

		if (!ops.isEmpty())
			section.put("op", ops);

		if (!guests.isEmpty())
			section.put("guest", guests);

		return section;
	}

	private void load(Map<String, List<String>> section) {
		ownerName = section.get("owner").get(0);
		location = Ini.loadLocation(section, "%s", plugin.getServer());
		isPublic = Boolean.valueOf(section.get("public").get(0));

		if (section.containsKey("guest"))
			for (String name : section.get("guest"))
				ranks.put(name, 1);

		if (section.containsKey("op"))
			for (String name : section.get("op"))
				ranks.put(name, 2);
	}
}
