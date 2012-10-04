package de.doridian.yiffbukkit.main;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.doridian.yiffbukkitsplit.util.PlayerHelper;

public abstract class ToolBind {
	public final String playerName;
	public final String name;

	public ToolBind(String name, Player ply) {
		this.name = name;
		playerName = ply == null ? null : ply.getName();
	}

	public boolean run(PlayerInteractEvent event) throws YiffBukkitCommandException { return false; };
	public boolean run(PlayerInteractEntityEvent event) throws YiffBukkitCommandException { return false; };

	private static Map<String, ToolBind> toolMappings = new HashMap<String, ToolBind>();

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
	 * @return true if there was a previous mapping
	 */
	public static boolean remove(Player ply, Material toolType, boolean left) {
		String key = makeKey(ply.getName(),toolType.name(), left);

		return toolMappings.remove(key) != null;
	}

	public static void removeGlobal(Material toolType) {
		String key = toolType.name();

		toolMappings.remove(key);
	}

	public static Map<String, ToolBind> list(String playerName) {
		Map<String, ToolBind> ret = new HashMap<String, ToolBind>();
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
		Map<String, ToolBind> ret = new HashMap<String, ToolBind>();
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
