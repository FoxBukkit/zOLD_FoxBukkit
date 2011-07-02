package de.doridian.yiffbukkit.commands;

import org.bukkit.command.CommandSender;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("bb")
@Help("Tries to emulate the /bb command from BigBrother using LogBlock.")
@Level(3)
@Permission("yiffbukkit.players.bb")
public class BigBrotherCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		if (args.length < 1)
			throw new YiffBukkitCommandException("Not enough arguments");

		String command = args[0];
		if (command.equals("here")) {
			int radius;
			String target;
			switch (args.length) {
			case 1:
				//bb here <name> - bb here with the default radius
				radius = 20;
				target = null;

				break;

			case 2:
				try {
					//bb here <radius> - bb here with the given radius
					radius = Integer.parseInt(args[1]);
					target = null;
				}
				catch (NumberFormatException e) {
					//bb here <name> - bb here for a specific player with the default radius
					radius = 20;
					target = playerHelper.completePlayerName(args[1], true);
				}
				break;

			default:
				try {
					//bb here <name> <radius> - bb here for a specific player with the given radius
					radius = Integer.parseInt(args[2]);
					target = playerHelper.completePlayerName(args[1], true);
				}
				catch (NumberFormatException e) {
					//bb here <name> <...> - not sure yet
					target = playerHelper.completePlayerName(args[2], true);

					try {
						//bb here <name> <radius> - bb here for a specific player with the given radius
						radius = Integer.parseInt(args[0]);
					}
					catch (NumberFormatException e2) {
						throw new YiffBukkitCommandException("Syntax error", e2);
					}
				}
				break;
			}

			if (target == null) {
				asPlayer(commandSender).chat("/lb area "+radius+" sum players");
			}
			else {
				asPlayer(commandSender).chat("/lb player "+target+" area "+radius+" sum blocks");
			}
		}
		else {
			throw new YiffBukkitCommandException("Invalid argument");
		}
	}
}
