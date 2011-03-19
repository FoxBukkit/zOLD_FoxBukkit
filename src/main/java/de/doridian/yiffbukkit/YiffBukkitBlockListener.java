package de.doridian.yiffbukkit;

import java.util.Hashtable;

import net.minecraft.server.TileEntity;
import net.minecraft.server.TileEntitySign;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockDamageLevel;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRightClickEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

import de.doridian.yiffbukkit.util.PlayerHelper;

/**
 * Handle events for all Block related events
 * @author Doridian
 */
public class YiffBukkitBlockListener extends BlockListener {
	private final YiffBukkit plugin;
	private Hashtable<Material,Integer> blocklevels = new Hashtable<Material,Integer>();
	private PlayerHelper playerHelper;

	public YiffBukkitBlockListener(YiffBukkit instance) {
		plugin = instance;
		playerHelper = plugin.playerHelper;

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
		pm.registerEvent(Event.Type.SIGN_CHANGE, this, Priority.Highest, plugin);
	}

	@Override
	public void onBlockPlace(BlockPlaceEvent event) {
		Player ply = event.getPlayer();
		if(playerHelper.isPlayerDisabled(ply)) {
			event.setBuild(false);
			return;
		}

		Material block = event.getBlock().getType();
		Integer selflvl = playerHelper.GetPlayerLevel(ply);
		if(selflvl < 0 || (blocklevels.containsKey(block) && selflvl < blocklevels.get(block))) {
			playerHelper.SendServerMessage(ply.getName() + " tried to spawn illegal block " + block.toString());
			event.setBuild(false);
		}
	}

	@Override
	public void onBlockRightClick(BlockRightClickEvent event) {
		ItemStack item = event.getItemInHand();
		Material itemMaterial = item.getType();
		if(itemMaterial == Material.AIR) return;

		Player ply = event.getPlayer();
		if(playerHelper.isPlayerDisabled(ply)) {
			item.setType(Material.GOLD_HOE);
			item.setAmount(1);
			item.setDurability(Short.MAX_VALUE);
			return;
		}

		Integer selflvl = playerHelper.GetPlayerLevel(ply);
		if(selflvl < 0 || (blocklevels.containsKey(itemMaterial) && selflvl < blocklevels.get(itemMaterial))) {
			playerHelper.SendServerMessage(ply.getName() + " tried to spawn illegal block " + itemMaterial.toString());
			item.setType(Material.GOLD_HOE);
			item.setAmount(1);
			item.setDurability(Short.MAX_VALUE);
			return;
		}

		// This will not be logged by bigbrother so I only allowed it for ops+ for now.
		// A fix would be to modify the event a bit to make BB log this. 
		if (selflvl >= 3 && itemMaterial == Material.INK_SACK) {
			Block block = event.getBlock();
			if (block.getType() == Material.WOOL) {
				block.setData((byte)(15 - item.getDurability()));
				int newAmount = item.getAmount()-1;
				if (newAmount > 0)
					item.setAmount(newAmount);
				else
					ply.setItemInHand(null);
			}
		}
	}

	@Override
	public void onBlockDamage(BlockDamageEvent event) {
		Player ply = event.getPlayer();
		if(playerHelper.isPlayerDisabled(ply)) {
			event.setCancelled(true);
			return;
		}

		if(playerHelper.GetPlayerLevel(ply) < 0 && event.getDamageLevel() == BlockDamageLevel.BROKEN) {
			playerHelper.SendServerMessage(ply.getName() + " tried to illegaly break a block!");
			event.setCancelled(true);
		}
	}

	@Override
	public void onSignChange(SignChangeEvent event) {
		Block block = event.getBlock();
		TileEntity tileEntity = ((CraftWorld)block.getWorld()).getHandle().getTileEntity(block.getX(),block.getY(),block.getZ());
		TileEntitySign tileEntitySign = (TileEntitySign) tileEntity;
		for (String line : tileEntitySign.a) {
			if (!line.isEmpty()) {
				for (int index = 0; index < 4; ++index) {
					event.setLine(index, tileEntitySign.a[index]);
				}
			}
		}
	}
}
