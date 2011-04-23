package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("clear")
@Help("Clears your inventory or another player's.")
@Usage("[<name>]")
@Level(4)
public class ClearCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		Player target;
		switch (args.length){
		case 0:
			//clear - clear inventory for self
			target = ply;
			break;

		case 1:
			//clear <name> - clear inventory of target
			target = playerHelper.MatchPlayerSingle(args[0]);
			break;

		default:
			throw new YiffBukkitCommandException("Syntax error");
		}

		Inventory inventory = target.getInventory();

		for (int i = 0; i < 39; i++) {
			inventory.setItem(i, null);
		}

		playerHelper.SendServerMessage(ply.getName() + " cleared " + target.getName() + "'s inventory.");
	}
}
