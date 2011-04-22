package de.doridian.yiffbukkit.commands;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import de.doridian.yiffbukkit.YiffBukkitCommandException;

public abstract class AbstractPlayerStateCommand extends ICommand {
	protected final Set<String> states = new HashSet<String>();

	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		Boolean newState;
		String targetName;
		final String commandSenderName = ply.getName();
		switch (args.length) {
		case 0:
			//state - toggle own state
			newState = null;
			targetName = commandSenderName;

			break;

		case 1:
			if ("on".equals(args[0])) {
				//state on - turn own state on
				newState = true;
				targetName = commandSenderName;
			}
			else if ("off".equals(args[0])) {
				//state off - turn own state off
				newState = false;
				targetName = commandSenderName;
			}
			else {
				//state <name> - toggle someone's state
				newState = null;
				targetName = playerHelper.CompletePlayerName(args[0], false);
				if (targetName == null)
					throw new YiffBukkitCommandException("No unique player found for '"+args[0]+"'");
			}
			break;

		default:
			if ("on".equals(args[0])) {
				//state on <name> - turn someone's state on
				newState = true;
				targetName = playerHelper.CompletePlayerName(args[1], false);
				if (targetName == null)
					throw new YiffBukkitCommandException("No unique player found for '"+args[1]+"'");
			}
			else if ("off".equals(args[0])) {
				//state off <name> - turn someone's state off
				newState = false;
				targetName = playerHelper.CompletePlayerName(args[1], false);
				if (targetName == null)
					throw new YiffBukkitCommandException("No unique player found for '"+args[1]+"'");
			}
			else {
				//state <name> <...> - not sure yet
				targetName = playerHelper.CompletePlayerName(args[0], false);
				if (targetName == null)
					throw new YiffBukkitCommandException("No unique player found for '"+args[0]+"'");

				if ("on".equals(args[1])) {
					//state <name> on - turn someone's state on
					newState = true;
				}
				else if ("off".equals(args[1])) {
					//state <name> off - turn someone's state off
					newState = false;
				}
				else {
					throw new YiffBukkitCommandException("Syntax error");
				}
			}
			break;
		}

		boolean prevState = states.contains(targetName);

		if (newState == null) {
			newState = !prevState;
		}

		onStateChange(prevState, newState, targetName, ply);

		if (newState) {
			states.add(targetName);
		}
		else {
			states.remove(targetName);
		}
	}

	protected abstract void onStateChange(boolean prevState, boolean newState, String targetName, final Player commandSender) throws YiffBukkitCommandException;
}
