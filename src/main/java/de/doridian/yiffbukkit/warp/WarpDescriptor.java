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
package de.doridian.yiffbukkit.warp;

import de.doridian.yiffbukkit.core.YiffBukkit;
import de.doridian.yiffbukkit.core.util.PlayerHelper;
import de.doridian.yiffbukkit.main.util.Ini;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class WarpDescriptor {
	private YiffBukkit plugin;
	private UUID ownerName;
	public String name;
	public Location location;
	public boolean isPublic = false;
	public boolean isHidden = false;
	/* ranks:
	 * 0 = not allowed
	 * 1 = guest, can port to warp
	 * 2 = op, can set guest permissions
	 * 3 = admin/owner, can remove warp
	 */
	private Map<UUID, Integer> ranks = new HashMap<>();

	public WarpDescriptor(YiffBukkit plugin, UUID ownerName, String name, Location location) {
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

	public int checkAccess(CommandSender commandSender) {
		if (commandSender == null)
			return 1;
		
		UUID playerName = commandSender.getUniqueId();
		if (playerName.equals(ownerName))
			return 3;

		int playerLevel = PlayerHelper.getPlayerLevel(commandSender);
		int ownerLevel = PlayerHelper.getPlayerLevel(ownerName);

		if (playerLevel > ownerLevel && commandSender.hasPermission("yiffbukkit.warp.override"))
			return 3;

		if (ranks.containsKey(playerName))
			return ranks.get(playerName);

		if (isPublic && playerLevel >= 1)
			return 1;

		return 0;
	}

	public void setAccess(CommandSender commandSender, Player player, int rank) throws WarpException {
		int commandSenderRank = checkAccess(commandSender);
		int playerRank = checkAccess(player);

		if (commandSenderRank <= playerRank)
			throw new WarpException("Permission denied: You do not exceed the target's rank!").setColor('4');

		if (commandSenderRank <= rank)
			throw new WarpException("Permission denied: You do not exceed the specified rank!").setColor('4');

		if (rank == 0) {
			ranks.remove(player.getUniqueId());
		}
		else {
			ranks.put(player.getUniqueId(), rank);
		}
	}

	public void setOwner(CommandSender commandSender, UUID newOwnerName) throws WarpException {
		if (checkAccess(commandSender) < 3)
			throw new WarpException("Permission denied: You do not own this warp!").setColor('4');

		ownerName = newOwnerName;
	}

	public UUID getOwner() {
		return ownerName;
	}

	public Map<UUID, Integer> getRanks() {
		return new Hashtable<>(ranks);
	}

	public Map<String, List<String>> save() {
		Map<String, List<String>> section = new TreeMap<>();

		section.put("owner", Arrays.asList(ownerName.toString()));

		Ini.saveLocation(section, "%s", location);
		section.put("public", Arrays.asList(String.valueOf(isPublic)));
		section.put("hidden", Arrays.asList(String.valueOf(isHidden)));

		List<String> ops = new ArrayList<>();
		List<String> guests = new ArrayList<>();
		for (Map.Entry<UUID, Integer> entry : ranks.entrySet()) {
			int rank = entry.getValue();
			switch (rank) {
			case 1:
				guests.add(entry.getKey().toString());
				break;

			case 2:
				ops.add(entry.getKey().toString());
				break;

			default:
				System.err.println("Invalid warp rank.");
				break;
			}
		}

		if (!ops.isEmpty())
			section.put("op", ops);

		if (!guests.isEmpty())
			section.put("guest", guests);

		return section;
	}

	private void load(Map<String, List<String>> section) {
		ownerName = UUID.fromString(section.get("owner").get(0));
		location = Ini.loadLocation(section, "%s");
		isPublic = Boolean.valueOf(section.get("public").get(0));
		isHidden = Boolean.valueOf(section.get("hidden").get(0));

		if (section.containsKey("guest"))
			for (String name : section.get("guest"))
				ranks.put(UUID.fromString(name), 1);

		if (section.containsKey("op"))
			for (String name : section.get("op"))
				ranks.put(UUID.fromString(name), 2);
	}
}
