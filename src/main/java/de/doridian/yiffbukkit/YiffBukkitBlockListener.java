package de.doridian.yiffbukkit;

import java.util.Hashtable;

import org.bukkit.Material;
import org.bukkit.block.BlockDamageLevel;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRightClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

/**
 * Handle events for all Block related events
 * @author Doridian
 */
public class YiffBukkitBlockListener extends BlockListener {
	private final YiffBukkit plugin;
	private Hashtable<Material,Integer> blocklevels = new Hashtable<Material,Integer>();

	public YiffBukkitBlockListener(YiffBukkit instance) {
		plugin = instance;

		blocklevels.put(Material.TNT, 4);
		blocklevels.put(Material.BEDROCK, 4);

		blocklevels.put(Material.OBSIDIAN, 1);

		blocklevels.put(Material.WATER, 1);
		blocklevels.put(Material.WATER_BUCKET, 1);
		blocklevels.put(Material.LAVA, 3);
		blocklevels.put(Material.LAVA_BUCKET, 3);

		blocklevels.put(Material.FLINT_AND_STEEL, 3);
		blocklevels.put(Material.FIRE, 3);

		PluginManager pm = plugin.getServer().getPluginManager();
		pm.registerEvent(Event.Type.BLOCK_PLACED, this, Priority.Normal, plugin);
		pm.registerEvent(Event.Type.BLOCK_RIGHTCLICKED, this, Priority.Normal, plugin);
		//pm.registerEvent(Event.Type.BLOCK_BREAK, this, Priority.Normal, plugin);
		pm.registerEvent(Event.Type.BLOCK_DAMAGED, this, Priority.Normal, plugin);
}

	public void onBlockPlace(BlockPlaceEvent event) {
		Player ply = event.getPlayer();
		if(ply.getHealth() <= 0) {
			event.setBuild(false);
			return;
		}
		
		Material block = event.getBlock().getType();
		Integer selflvl = plugin.playerHelper.GetPlayerLevel(ply);
		if(selflvl < 0 || (blocklevels.containsKey(block) && selflvl < blocklevels.get(block))) {
			plugin.playerHelper.SendServerMessage(ply.getName() + " tried to spawn illegal block " + block.toString());
			event.setBuild(false);
		}
	}

	public void onBlockRightClick(BlockRightClickEvent event) {
		Material block = event.getItemInHand().getType();
		if(block == Material.AIR) return;
		
		Player ply = event.getPlayer();
		if(ply.getHealth() <= 0) {
			ItemStack item = event.getItemInHand();
			item.setType(Material.GOLD_HOE);
			item.setAmount(1);
			item.setDurability(Short.MAX_VALUE);
			return;
		}
		
		Integer selflvl = plugin.playerHelper.GetPlayerLevel(ply);
		if(selflvl < 0 || (blocklevels.containsKey(block) && selflvl < blocklevels.get(block))) {
			plugin.playerHelper.SendServerMessage(ply.getName() + " tried to spawn illegal block " + block.toString());
			ItemStack item = event.getItemInHand();
			item.setType(Material.GOLD_HOE);
			item.setAmount(1);
			item.setDurability(Short.MAX_VALUE);
		}
	}

	public void onBlockDamage(BlockDamageEvent event) {
		Player ply = event.getPlayer();
		if(ply.getHealth() <= 0) {
			event.setCancelled(true);
			return;
		}
		
		if(plugin.playerHelper.GetPlayerLevel(ply) < 0 && event.getDamageLevel() == BlockDamageLevel.BROKEN) {
			plugin.playerHelper.SendServerMessage(ply.getName() + " tried to illegaly break a block!");
			event.setCancelled(true);
		}
	}
}
