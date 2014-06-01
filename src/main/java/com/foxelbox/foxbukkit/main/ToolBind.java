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
package com.foxelbox.foxbukkit.main;

import com.foxelbox.foxbukkit.core.util.PlayerHelper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public abstract class ToolBind {
	public final String playerName;
	public final String name;

	public ToolBind(String name, Player ply) {
		this.name = name;
		playerName = ply == null ? null : ply.getName();
	}

	/**
	 * Called when the bind is triggers by interacting with a block.
	 *
	 * @param event the event that triggered the bind
	 * @return false if the bind was not applicable
	 * @throws FoxBukkitCommandException if there was a problem
	 */
	public boolean run(PlayerInteractEvent event) throws FoxBukkitCommandException { return false; }

	/**
	 * Called when the bind is triggers by interacting with an entity.
	 *
	 * @param event the event that triggered the bind
	 * @return false if the bind was not applicable
	 * @throws FoxBukkitCommandException if there was a problem
	 */
	public boolean run(PlayerInteractEntityEvent event) throws FoxBukkitCommandException { return false; }

	public String getRestoreCommand(String toolName, boolean left) { return null; }

	private static Map<String, ToolBind> toolMappings = new HashMap<>();

	public static void add(Player ply, Material toolType, boolean left, ToolBind toolBind) {
		String key = makeKey(ply.getName(),toolType.name(), left);

		toolMappings.put(key, toolBind);
	}

	public static void addGlobal(Material toolType, ToolBind toolBind) {
		String key = toolType.name();

		toolMappings.put(key, toolBind);
	}

	/**
	 * Removes a tool mapping for the given player/tool pair.
	 *
	 * @param ply the player the mapping is associated with.
	 * @param toolType the tool the mapping is associated with.
	 * @return the previous mapping, if there was one
	 */
	public static ToolBind remove(Player ply, Material toolType, boolean left) {
		return toolMappings.remove(makeKey(ply.getName(),toolType.name(), left));
	}

	public static ToolBind removeGlobal(Material toolType) {
		return toolMappings.remove(toolType.name());
	}

	public static Map<String, ToolBind> list(String playerName) {
		Map<String, ToolBind> ret = new HashMap<>();
		for (Entry<String, ToolBind> entry : toolMappings.entrySet()) {
			ToolBind toolBind = entry.getValue();
			if (!playerName.equals(toolBind.playerName))
				continue;

			String toolName = entry.getKey();
			toolName = toolName.substring(toolName.indexOf(' ')+1);

			ret.put(toolName, toolBind);
		}
		return ret;
	}

	public static Map<String, ToolBind> listGlobal() {
		Map<String, ToolBind> ret = new HashMap<>();
		for (Entry<String, ToolBind> entry : toolMappings.entrySet()) {
			ToolBind toolBind = entry.getValue();
			if (toolBind.playerName != null)
				continue;

			ret.put(entry.getKey(), toolBind);
		}
		return ret;
	}

	public static void updateToolMappings(Player player) {
		String playerName = player.getName();
		for (Entry<String, ToolBind> entry : toolMappings.entrySet()) {
			ToolBind toolBind = entry.getValue();
			if (playerName.equals(toolBind.playerName)) {
				String toolName = entry.getKey();
				toolName = toolName.substring(toolName.indexOf(' ')+1);
				PlayerHelper.sendDirectedMessage(player, "Restored bind \u00a7e"+toolName+"\u00a7f => \u00a79"+toolBind.name);
			}
		}
	}

	public static ToolBind get(String playerName, Material itemMaterial, boolean left) {
		final String itemName = itemMaterial.name();
		final ToolBind toolBind = toolMappings.get(makeKey(playerName, itemName, left));
		if (toolBind != null)
			return toolBind;

		return toolMappings.get(itemName);
	}

	private static String makeKey(String playerName, final String itemName, boolean left) {
		return playerName+" "+itemName+left;
	}
}
