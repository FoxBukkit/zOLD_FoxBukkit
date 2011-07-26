package de.doridian.yiffbukkit.commands;

import org.bukkit.command.CommandSender;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("filter")
@Help("§cPermanently §ffilters out text from the chat log. Use the -a flag to affect all players. Google \"java api pattern\" for regex help.")
@Usage("[-a] <regex>")
@BooleanFlags("a")
@Permission("yiffbukkit.chatmanager.filter")
public class FilterCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		if (booleanFlags.contains('a'))
			plugin.chatManager.filterChats(argStr);
		else
			plugin.chatManager.filterChat(argStr, asPlayer(commandSender));
	}
}
