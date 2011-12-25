package de.doridian.yiffbukkit.commands;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.BooleanFlags;
import de.doridian.yiffbukkit.commands.ICommand.Help;
import de.doridian.yiffbukkit.commands.ICommand.Names;
import de.doridian.yiffbukkit.commands.ICommand.Permission;
import de.doridian.yiffbukkit.commands.ICommand.Usage;
import org.bukkit.command.CommandSender;

@Names("filter")
@Help("\u00a7cPermanently \u00a7ffilters out text from the chat log. Use the -a flag to affect all players. Google \"java api pattern\" for regex help.")
@Usage("[-a] <regex>")
@BooleanFlags("a")
@Permission("yiffbukkit.chatmanager.filter")
public class FilterCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		argStr = parseFlags(argStr);

		if (booleanFlags.contains('a'))
			plugin.chatManager.filterChats(argStr);
		else
			plugin.chatManager.filterChat(argStr, asPlayer(commandSender));
	}
}
