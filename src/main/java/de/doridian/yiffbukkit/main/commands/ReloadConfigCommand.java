package de.doridian.yiffbukkit.main.commands;

import de.doridian.yiffbukkit.main.StateContainer;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;

import org.bukkit.command.CommandSender;

@Names("reloadconf")
@Help("Reloads a named config.")
@Usage("")
@Permission("yiffbukkit.reload")
public class ReloadConfigCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		final boolean success;
		try {
			success = StateContainer.loadSingle(argStr);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new YiffBukkitCommandException("Exception caught while loading config. See Log.", e);
		}

		if (!success)
			throw new YiffBukkitCommandException("Config not found");

		PlayerHelper.sendDirectedMessage(commandSender, "Reloaded "+argStr+" config.");
	}
}
