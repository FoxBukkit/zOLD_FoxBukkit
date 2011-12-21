package de.doridian.yiffbukkit.commands;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import org.bukkit.entity.Player;

@ICommand.Names("yiffcraft")
@ICommand.Help("Command used by the YC client")
@ICommand.Level(0)
public class YiffcraftCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		if(argStr.equalsIgnoreCase("getcommands")) {
			ply.sendRawMessage("\u00a7f\u00a75\u00a7d" + 'c' + "http://mc.doridian.de/yb_commands.txt");
		}
	}
}
