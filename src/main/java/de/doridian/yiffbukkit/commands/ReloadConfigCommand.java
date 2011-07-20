package de.doridian.yiffbukkit.commands;
import org.bukkit.command.CommandSender;

import de.doridian.yiffbukkit.StateContainer;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("reloadconf")
@Help("Reloads a named config.")
@Usage("")
@Permission("yiffbukkit.reload")
public class ReloadConfigCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		try {
			if (!StateContainer.loadSingle(argStr))
				throw new YiffBukkitCommandException("Config not found");
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new YiffBukkitCommandException("Exception caught while loading config. See Log.", e);
		}
		playerHelper.sendDirectedMessage(commandSender, "Reloaded "+argStr+" config.");
	}
}
