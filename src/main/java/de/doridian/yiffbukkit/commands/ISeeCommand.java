package de.doridian.yiffbukkit.commands;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.InventoryPlayer;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.util.PlayerFindException;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("isee")
@Help("Opens the inventory of target player as a chest")
@Usage("<player>")
@Level(4)
@Permission("yiffbukkit.players.isee")
public class ISeeCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws PlayerFindException {
		Player otherply = playerHelper.matchPlayerSingle(args[0]);

		// Get the EntityPlayer handle from the sender
		EntityPlayer eply = ((CraftPlayer)ply).getHandle();

		// Get the Human Entity from the Target
		EntityHuman eotherply = ((CraftPlayer)otherply).getHandle();

		// Get's the targets inventory
		InventoryPlayer eotherinventory = eotherply.inventory;

		eply.a(eotherinventory); // Show to the user.
	}
}
