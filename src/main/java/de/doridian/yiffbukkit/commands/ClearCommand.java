package de.doridian.yiffbukkit.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("clear")
@Help("Clears your inventory or another player's. Use -a to include the toolbar.")
@Usage("[-a] [<name>]")
@Level(4)
@BooleanFlags("a")
@Permission("yiffbukkit.players.clear")
public class ClearCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		args = parseFlags(args);
		Player target;
		switch (args.length){
		case 0:
			//clear - clear inventory for self
			target = asPlayer(commandSender);
			break;

		case 1:
			//clear <name> - clear inventory of target
			target = playerHelper.matchPlayerSingle(args[0]);
			break;

		default:
			throw new YiffBukkitCommandException("Syntax error");
		}

		Inventory inventory = target.getInventory();

		final int startIndex = booleanFlags.contains('a') ? 0 : 9;
		
		for (int i = startIndex; i < 36; i++) {
			inventory.setItem(i, null);
		}

		playerHelper.sendServerMessage(commandSender.getName() + " cleared " + target.getName() + "'s inventory.");
	}
}
