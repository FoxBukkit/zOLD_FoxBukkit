package de.doridian.yiffbukkit.commands;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.InventoryPlayer;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkitPlayerListener;
import de.doridian.yiffbukkit.util.PlayerFindException;

public class ISeeCommand extends ICommand {
	public int GetMinLevel() {
		return 4;
	}

	public ISeeCommand(YiffBukkitPlayerListener playerListener) {
		super(playerListener);
	}

	public void Run(Player ply, String[] args, String argStr) throws PlayerFindException {
		Player otherply = playerHelper.MatchPlayerSingle(args[0]);

		// Get the EntityPlayer handle from the sender
		EntityPlayer eply = ((CraftPlayer)ply).getHandle();

		// Get the Human Entity from the Target
		EntityHuman eotherply = ((CraftPlayer)otherply).getHandle();

		// Get's the targets inventory
		InventoryPlayer eotherinventory = eotherply.inventory;

		eply.a(eotherinventory); // Show to the user.
	}

	public String GetHelp() {
		return "Opens the inventory of target player as a chest";
	}

	public String GetUsage() {
		return "<player>";
	}
}
