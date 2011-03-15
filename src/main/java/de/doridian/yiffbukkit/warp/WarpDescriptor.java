package de.doridian.yiffbukkit.warp;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
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
		if (playerName.equals(ownerName))
			return 3;

		int playerLevel = plugin.playerHelper.GetPlayerLevel(playerName);
		int ownerLevel = plugin.playerHelper.GetPlayerLevel(ownerName);

		if (playerLevel > ownerLevel && playerLevel >= 3)
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

		if (commandSenderRank <= Math.max(playerRank, rank)) {
			throw new WarpException("Permission denied");
		}

		if (rank == 0) {
			ranks.remove(playerName);
		}
		else {
			ranks.put(playerName, rank);
		}
	}

	public void setOwner(String commandSenderName, String newOwnerName) throws WarpException {
		if (checkAccess(commandSenderName) < 3)
			throw new WarpException("You need to be the warp's owner to do this.");

		ownerName = newOwnerName;
	}

	public String getOwner() {
		return ownerName;
	}

	public Map<String, Integer> getRanks() {
		return new Hashtable<String, Integer>(ranks);
	}

	public void save(BufferedWriter stream) throws IOException {
		stream.write("owner=");
		stream.write(ownerName);
		stream.newLine();

		stream.write("world=");
		stream.write(location.getWorld().getName());
		stream.newLine();

		stream.write("x=");
		stream.write(String.valueOf(location.getX()));
		stream.newLine();

		stream.write("y=");
		stream.write(String.valueOf(location.getY()));
		stream.newLine();

		stream.write("z=");
		stream.write(String.valueOf(location.getZ()));
		stream.newLine();

		stream.write("pitch=");
		stream.write(String.valueOf(location.getPitch()));
		stream.newLine();

		stream.write("yaw=");
		stream.write(String.valueOf(location.getYaw()));
		stream.newLine();

		stream.write("public=");
		stream.write(String.valueOf(isPublic));
		stream.newLine();

		for (Map.Entry<String, Integer> entry : ranks.entrySet()) {
			int rank = entry.getValue();
			if (rank == 1)
				stream.write("guest=");
			else if (rank == 2)
				stream.write("op=");
			else {
				System.err.println("Invalid warp rank.");
				continue;
			}
			stream.write(entry.getKey());
			stream.newLine();
		}
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
