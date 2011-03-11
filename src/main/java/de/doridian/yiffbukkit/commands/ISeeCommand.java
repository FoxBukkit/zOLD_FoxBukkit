package de.doridian.yiffbukkit.commands;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.InventoryPlayer;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.util.PlayerFindException;

public class ISeeCommand extends ICommand {
	public int GetMinLevel() {
		return 4;
	}

	public ISeeCommand(YiffBukkit plug) {
		super(plug);
	}

	public void Run(Player ply, String[] args, String argStr) throws PlayerFindException {
		Player otherply = playerHelper.MatchPlayerSingle(args[0]);

		// Get the EntityPlayer handle from the sender
		EntityPlayer eh = ((CraftPlayer)ply).getHandle();

		// Get the Human Entity from the Target
		EntityHuman ehtarget = ((CraftPlayer)otherply).getHandle();

		// Get's the targets inventory
		InventoryPlayer ehtargetinv = ehtarget.inventory;

		eh.a(ehtargetinv); // Show to the user.
	}

	public String GetHelp() {
		return "Opens the inventory of target player as a chest";
	}

	public String GetUsage() {
		return "<player>";
	}
}
