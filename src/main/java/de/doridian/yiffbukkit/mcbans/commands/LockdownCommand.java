package de.doridian.yiffbukkit.mcbans.commands;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.ICommand;
import de.doridian.yiffbukkit.main.commands.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.ICommand.Usage;
import org.bukkit.command.CommandSender;

@Names("lockdown")
@Help("Locks or unlocks the server for guests")
@Usage("[on|off]")
@Permission("yiffbukkit.users.lockdown")
public class LockdownCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		final String name = commandSender.getName();
		if (plugin.serverClosed) {
			if (argStr.equals("on"))
				throw new YiffBukkitCommandException("The server is already locked!");

			plugin.serverClosed = false;
			playerHelper.sendServerMessage(name + " unlocked the server for guests.", 1);
		}
		else {
			if (argStr.equals("off"))
				throw new YiffBukkitCommandException("The server is already unlocked!");

			plugin.serverClosed = true;
			playerHelper.sendServerMessage(name + " locked the server for guests.", 1);
		}

	}
}
