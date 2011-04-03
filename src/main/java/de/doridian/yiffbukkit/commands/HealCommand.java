package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;
import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.YiffBukkitCommandException;

public class HealCommand extends ICommand {
	public HealCommand(YiffBukkit plug) {
		super(plug);
	}

	@Override
	public int GetMinLevel() {
		return 4;
	}

	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		int amount;
		Player target;
		switch (args.length){
		case 0:
			//heal <name> - heal yourself fully
			amount = 20;
			target = ply;

			break;

		case 1:
			try {
				//heal <amount> - heal yourself by the given amount
				amount = Integer.parseInt(args[0]);
				target = ply;
			}
			catch (NumberFormatException e) {
				//heal <name> - heal someone fully
				amount = 20;
				target = playerHelper.MatchPlayerSingle(args[0]);
			}
			break;

		default:
			try {
				//god on <name> - heal someone by the given amount
				amount = Integer.parseInt(args[0]);
				target = playerHelper.MatchPlayerSingle(args[1]);
			}
			catch (NumberFormatException e) {
				//heal <name> <...> - not sure yet
				target = playerHelper.MatchPlayerSingle(args[0]);

				try {
					//heal <name> amount - heal someone by the given amount
					amount = Integer.parseInt(args[1]);
				}
				catch (NumberFormatException e2) {
					throw new YiffBukkitCommandException("Syntax error", e2);
				}
			}
			break;
		}

		target.setHealth(Math.min(20, target.getHealth() + amount));

		if (amount >= 20)
			playerHelper.SendServerMessage(ply.getName() + " fully healed " + target.getName() + ".");
		else
			playerHelper.SendServerMessage(ply.getName() + " healed " + target.getName() + " by "+amount+" points.");
	}

	@Override
	public String GetHelp() {
		return "Heals a player fully or by the given amount..";
	}

	@Override
	public String GetUsage() {
		return "[<name>] [<amount>]";
	}
}
