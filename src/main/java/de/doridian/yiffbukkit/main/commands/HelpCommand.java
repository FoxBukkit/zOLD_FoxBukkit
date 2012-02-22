package de.doridian.yiffbukkit.main.commands;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.ICommand.Usage;
import org.bukkit.command.CommandSender;

import java.util.Map;

@Names("help")
@Help("Prints a list of available commands or information about the specified command.")
@Usage("[<command>]")
@Permission("yiffbukkit.help")
public class HelpCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		Map<String, ICommand> commands = plugin.commandSystem.getCommands();

		if(args.length > 0) {
			ICommand val = commands.get(args[0]);
			if (val == null || !val.canPlayerUseCommand(commandSender))
				throw new YiffBukkitCommandException("Command not found!");

			for (String line : val.getHelp().split("\n")) {
				playerHelper.sendDirectedMessage(commandSender, line);
			}
			playerHelper.sendDirectedMessage(commandSender, "Usage: /" + args[0] + " " + val.getUsage());
		}
		else {
			String ret = "Available commands: /";
			for (String key : commands.keySet()) {
				if (key == "\u00a7")
					continue;

				ICommand val = commands.get(key);
				if (!val.canPlayerUseCommand(commandSender))
					continue;

				ret += key + ", /";
			}
			ret = ret.substring(0,ret.length() - 3);
			playerHelper.sendDirectedMessage(commandSender, ret);
		}
	}
}
