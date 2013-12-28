package de.doridian.yiffbukkit.bans.commands;

import de.doridian.yiffbukkit.bans.LockDownMode;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;
import org.bukkit.command.CommandSender;

import java.io.IOException;

@Names("lockdown")
@Help("Locks or unlocks the server for guests")
@Usage("[on|off]")
@Permission("yiffbukkit.users.lockdown")
public class LockdownCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		final String name = commandSender.getName();
		argStr = argStr.toUpperCase();
		final LockDownMode newMode;
		if (argStr.isEmpty()) {
			newMode = plugin.bans.lockdownMode == LockDownMode.OFF ? LockDownMode.KICK : LockDownMode.OFF;
		}
		else if (argStr.equals("ON")){
			newMode = LockDownMode.KICK;
		}
		else {
			try {
				newMode = LockDownMode.valueOf(argStr);
			}
			catch (IllegalArgumentException e) {
				throw new YiffBukkitCommandException("Invalid lockdown mode.", e);
			}
		}

		if (newMode == plugin.bans.lockdownMode) {
			throw new YiffBukkitCommandException(newMode.getDescription());
		}

		plugin.bans.lockdownMode = newMode;

		if (plugin.bans.lockdownMode == LockDownMode.FIREWALL) {
			try {
				Runtime.getRuntime().exec("./wally F");
				System.out.println("Flushed blocked IPs.");
			} catch (IOException e) {
				System.out.println("Failed to flush blocked IPs.");
			}
		}

		if (newMode == LockDownMode.OFF) {
			playerHelper.sendServerMessage(name + " unlocked the server for guests.", 1);
		}
		else {
			playerHelper.sendServerMessage(name + " locked the server for guests.", 1);
		}

	}
}
