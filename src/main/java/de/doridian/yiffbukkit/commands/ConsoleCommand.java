package de.doridian.yiffbukkit.commands;

import org.bukkit.command.CommandSender;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("rcon")
@Help("Pushes a command to console")
@Usage("<command>")
@Level(5)
public class ConsoleCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) {
		//String[] argsX = new String[args.length - 1];
		//System.arraycopy(args, 1, argsX, 0, argsX.length);
		//plugin.getServer().
		//plugin.getServer().getPluginCommand(args[0]).execute(ply, args[0], argsX);
		YiffBukkit.sendServerCmd(argStr, commandSender);
	}
}
