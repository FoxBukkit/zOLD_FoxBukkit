package de.doridian.yiffbukkit.main.commands;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractPlayerStateCommand extends ICommand {
	protected final Set<String> states = new HashSet<>();

	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		Boolean newState;
		String targetName;
		switch (args.length) {
		case 0:
			//state - toggle own state
			newState = null;
			targetName = asPlayer(commandSender).getName();

			break;

		case 1:
			switch (args[0]) {
			case "on":
				//state on - turn own state on
				newState = true;
				targetName = asPlayer(commandSender).getName();
				break;

			case "off":
				//state off - turn own state off
				newState = false;
				targetName = asPlayer(commandSender).getName();
				break;
			default:

				//state <name> - toggle someone's state
				newState = null;
				targetName = playerHelper.completePlayerName(args[0], false);
				if (targetName == null)
					throw new YiffBukkitCommandException("No unique player found for '" + args[0] + "'");
				break;
			}
			break;

		default:
			switch (args[0]) {
			case "on":
				//state on <name> - turn someone's state on
				newState = true;
				targetName = playerHelper.completePlayerName(args[1], false);
				if (targetName == null)
					throw new YiffBukkitCommandException("No unique player found for '" + args[1] + "'");
				break;

			case "off":
				//state off <name> - turn someone's state off
				newState = false;
				targetName = playerHelper.completePlayerName(args[1], false);
				if (targetName == null)
					throw new YiffBukkitCommandException("No unique player found for '" + args[1] + "'");
				break;

			default:
				//state <name> <...> - not sure yet
				targetName = playerHelper.completePlayerName(args[0], false);
				if (targetName == null)
					throw new YiffBukkitCommandException("No unique player found for '" + args[0] + "'");

				switch (args[1]) {
				case "on":
					//state <name> on - turn someone's state on
					newState = true;
					break;

				case "off":
					//state <name> off - turn someone's state off
					newState = false;
					break;

				default:
					throw new YiffBukkitCommandException("Syntax error");
				}
				break;
			}
			break;
		}

		boolean prevState = states.contains(targetName);

		if (newState == null) {
			newState = !prevState;
		}

		onStateChange(prevState, newState, targetName, commandSender);

		if (newState) {
			states.add(targetName);
		}
		else {
			states.remove(targetName);
		}
	}

	protected abstract void onStateChange(boolean prevState, boolean newState, String targetName, CommandSender commandSender) throws YiffBukkitCommandException;
}
