package de.doridian.yiffbukkit.commands;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import de.doridian.yiffbukkit.YiffBukkit;

public class GiveCommand extends ICommand {
	public int GetMinLevel() {
		return 3;
	}

	public GiveCommand(YiffBukkit plug) {
		super(plug);
	}

	public void Run(Player ply, String[] args, String argStr) {
		Integer count = 1;
		String otherName = null;
		try {
			count = Integer.valueOf(args[1]);
			if (args.length >= 3)
				otherName = args[2];
		}
		catch(Exception e) {
			if (args.length >= 2)
				otherName = args[1];
		}
		ItemStack stack = new ItemStack(Material.matchMaterial(args[0].replace('_',' ')), count);

		if (otherName == null) {
			PlayerInventory inv = ply.getInventory();
			int empty = inv.firstEmpty();
			inv.setItem(empty, stack);
			playerHelper.SendDirectedMessage(ply, "Item has been put in first free slot of your inventory!");
		}
		else {
			Player otherply = playerHelper.MatchPlayerSingle(ply, otherName);
			if (otherply == null)
				return;

			PlayerInventory inv = otherply.getInventory();
			int empty = inv.firstEmpty();
			inv.setItem(empty, stack);
			playerHelper.SendDirectedMessage(ply, "Item has been put in first free slot of "+otherply.getName()+"'s inventory!");
		}
	}

	public String GetHelp() {
		return "Gives resource (use _ for spaces in name!)";
	}

	public String GetUsage() {
		return "<name or id> [<amount>] [<player>]";
	}
}
