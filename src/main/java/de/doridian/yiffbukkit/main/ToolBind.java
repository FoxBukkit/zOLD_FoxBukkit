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

	public static Map<String, ToolBind> toolMappings = new HashMap<String, ToolBind>();

	public static void add(Player ply, Material toolType, ToolBind toolBind) {
		String key = ply.getName()+" "+toolType.name();

		toolMappings.put(key, toolBind);
	}

	public static void addGlobal(Material toolType, ToolBind toolBind) {
		String key = toolType.name();

		toolMappings.put(key, toolBind);
	}

	public static void remove(Player ply, Material toolType) {
		String key = ply.getName()+" "+toolType.name();

		toolMappings.remove(key);
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

	public static ToolBind get(String playerName, Material itemMaterial) {
		final String itemName = itemMaterial.name();
		final ToolBind toolBind = toolMappings.get(playerName+" "+itemName);
		if (toolBind != null)
			return toolBind;

		return toolMappings.get(itemName);
	}
}
