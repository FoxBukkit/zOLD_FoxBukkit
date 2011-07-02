package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("lockdown")
@Help("Locks or unlocks the server for guests")
@Usage("[on|off]")
@Level(5)
@Permission("yiffbukkit.users.lockdown")
public class LockdownCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		final String playerName = ply.getName();
		if (plugin.serverClosed) {
			if (argStr.equals("on"))
				throw new YiffBukkitCommandException("The server is already locked!");

			plugin.serverClosed = false;
			playerHelper.sendServerMessage(playerName + " unlocked the server for guests.", 1);
		}
		else {
			if (argStr.equals("off"))
				throw new YiffBukkitCommandException("The server is already unlocked!");

			plugin.serverClosed = true;
			playerHelper.sendServerMessage(playerName + " locked the server for guests.", 1);
		}

	}
}
