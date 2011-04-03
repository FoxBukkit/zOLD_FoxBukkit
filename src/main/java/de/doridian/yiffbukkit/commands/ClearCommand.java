package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.YiffBukkitCommandException;

public class ClearCommand extends ICommand {
	public ClearCommand(YiffBukkit plug) {
		super(plug);
	}

	@Override
	public int GetMinLevel() {
		return 4;
	}

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

	@Override
	public String GetHelp() {
		return "Clears your inventory or another players.";
	}

	@Override
	public String GetUsage() {
		return "[<name>]";
	}
}
