package de.doridian.yiffbukkit.commands;

import java.util.Enumeration;
import java.util.Hashtable;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("help")
@Help("Prints command list if used without parameters or information about the specified command")
@Usage("[<command>]")
@Level(0)
public class HelpCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) {
		int selflevel = playerHelper.GetPlayerLevel(ply);
		Hashtable<String,ICommand> commands = plugin.GetCommands();

		if(args.length > 0) {
			ICommand val = commands.get(args[0]);
			if(val == null || val.GetMinLevel() > selflevel) {
				playerHelper.SendDirectedMessage(ply, "Command not found!");
				return;
			}
			for (String line : val.GetHelp().split("\n")) {
				playerHelper.SendDirectedMessage(ply, line);
			}
			playerHelper.SendDirectedMessage(ply, "Usage: /" + args[0] + " " + val.GetUsage());
		}
		else {
			String ret = "Available commands: /";
			Enumeration<String> e = commands.keys();
			while(e.hasMoreElements()) {
				String key = e.nextElement();
				if (key == "§")
					continue;

				ICommand val = commands.get(key);
				if(val.GetMinLevel() > selflevel)
					continue;

				ret += key + ", /";
			}
			ret = ret.substring(0,ret.length() - 3);
			playerHelper.SendDirectedMessage(ply, ret);
		}
	}
}
