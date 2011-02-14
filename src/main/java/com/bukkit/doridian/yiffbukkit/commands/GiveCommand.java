package com.bukkit.doridian.yiffbukkit.commands;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.bukkit.doridian.yiffbukkit.YiffBukkit;

public class GiveCommand extends ICommand {
	public int GetMinLevel() {
		return 3;
	}
	
	public GiveCommand(YiffBukkit plug) {
		plugin = plug;
	}

	public void Run(Player ply, String[] args, String argStr) {
		PlayerInventory inv = ply.getInventory();
		int empty = inv.firstEmpty();
		Integer count = 1;
		try {
			count = Integer.valueOf(args[1]);
		}
		catch(Exception e) { }
		ItemStack stack = new ItemStack(Material.matchMaterial(args[0].replace('_',' ')), count);
		inv.setItem(empty, stack);
		plugin.playerHelper.SendDirectedMessage(ply, "Item has been put in first free slot of your inventory!");
	}
	
	public String GetHelp() {
		return "Gives resource (use _ for spaces in name!)";
	}

	public String GetUsage() {
		return "<name or id> [amount]";
	}
}
