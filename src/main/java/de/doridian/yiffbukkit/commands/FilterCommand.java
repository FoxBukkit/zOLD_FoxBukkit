package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("filter")
@Help("$cPermanently $ffilters out text from the chat log.")
@Usage("<regex>")
@Level(4) 
public class FilterCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		plugin.chatManager.filterChat(argStr);
	}
}
