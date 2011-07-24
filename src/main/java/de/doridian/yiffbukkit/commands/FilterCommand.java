package de.doridian.yiffbukkit.commands;

import org.bukkit.command.CommandSender;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("filter")
@Help("§cPermanently §ffilters out text from the chat log.")
@Usage("<regex>")
@Permission("yiffbukkit.chatmanager.filter")
public class FilterCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		plugin.chatManager.filterChat(argStr);
	}
}
