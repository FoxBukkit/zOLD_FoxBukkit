package de.doridian.yiffbukkit.warp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;

import de.doridian.yiffbukkit.YiffBukkit;

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

	public WarpDescriptor(YiffBukkit plugin, String name, BufferedReader stream) throws IOException {
		this.plugin = plugin;
		this.name = name;
		load(stream);
	}

	public int checkAccess(String playerName) {
		if (playerName.equals(ownerName))
			return 3;

		int playerLevel = plugin.playerHelper.GetPlayerLevel(playerName);
		int ownerLevel = plugin.playerHelper.GetPlayerLevel(ownerName);

		if (playerLevel > ownerLevel && playerLevel >= 3)
			return 3;

		if (isPublic)
			return 1;

		Integer rank = ranks.get(playerName);
		if (rank == null)
			return 0;

		return rank;
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

	private void load(BufferedReader stream) throws IOException {
		location = new Location(null, 0, 0, 0);

		Pattern linePattern = Pattern.compile("^([^=]+)=(.*)$");
		String line;
		while((line = stream.readLine()) != null) {
			if (line.trim().isEmpty())
				break;

			Matcher matcher = linePattern.matcher(line);

			if (!matcher.matches()) {
				System.err.println("Malformed line in warps.txt.");
				continue;
			}

			String key = matcher.group(1);
			String value = matcher.group(2);

			if (key.equals("owner")) {
				ownerName = value;
			}
			else if (key.equals("world")) {
				location.setWorld(plugin.getServer().getWorld(value));
			}
			else if (key.equals("x")) {
				location.setX(Double.valueOf(value));
			}
			else if (key.equals("y")) {
				location.setY(Double.valueOf(value));
			}
			else if (key.equals("z")) {
				location.setZ(Double.valueOf(value));
			}
			else if (key.equals("pitch")) {
				location.setPitch(Float.valueOf(value));
			}
			else if (key.equals("yaw")) {
				location.setYaw(Float.valueOf(value));
			}
			else if (key.equals("public")) {
				isPublic = Boolean.valueOf(value);
			}
			else if (key.equals("guest")) {
				ranks.put(value, 1);
			}
			else if (key.equals("op")) {
				ranks.put(value, 2);
			}
			else {
				System.err.println("Unknown key in warps.txt.");
			}
		}
	}
}
