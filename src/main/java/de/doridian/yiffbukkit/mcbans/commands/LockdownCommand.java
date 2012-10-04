package de.doridian.yiffbukkit.mcbans.commands;

import java.io.IOException;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;
import de.doridian.yiffbukkitsplit.LockDownMode;

import org.bukkit.command.CommandSender;

@Names("lockdown")
@Help("Locks or unlocks the server for guests")
@Usage("[on|off]")
@Permission("yiffbukkit.users.lockdown")
public class LockdownCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		final String name = commandSender.getName();
		if (argStr.isEmpty()) {
			argStr = plugin.lockdownMode == LockDownMode.NONE ? "KICK" : "NONE";
		}

		argStr = argStr.toUpperCase();
		if (argStr.equals("ON")){
			argStr = "KICK";
		}
		else if (argStr.equals("OFF")) {
			argStr = "NONE";
		}

		LockDownMode newMode = LockDownMode.valueOf(argStr);

		if (newMode == plugin.lockdownMode) {
			throw new YiffBukkitCommandException(newMode.getDescription());
		}

		plugin.lockdownMode = newMode;

		if (plugin.lockdownMode == LockDownMode.FIREWALL) {
			try {
				Runtime.getRuntime().exec("./wally F");
				System.out.println("Flushed blocked IPs.");
			} catch (IOException e) {
				System.out.println("Failed to flush blocked IPs.");
			}
		}

		if (newMode == LockDownMode.NONE) {
			playerHelper.sendServerMessage(name + " unlocked the server for guests.", 1);
		}
		else {
			playerHelper.sendServerMessage(name + " locked the server for guests.", 1);
		}

	}
}
