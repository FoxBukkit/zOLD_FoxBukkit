package de.doridian.yiffbukkit.commands;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;
import de.doridian.yiffbukkit.ssl.SSLUtils;
import de.doridian.yiffbukkit.util.Configuration;
import org.bukkit.entity.Player;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Map;

@Names("yiffcraft")
@Help("Command used by the YC client")
@Level(0)
public class YiffcraftCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		if(argStr.equalsIgnoreCase("getcommands")) {
			plugin.playerHelper.setYiffcraftState(ply, true);
			plugin.playerHelper.sendYiffcraftClientCommand(ply, 'c', Configuration.getValue("yiffcraft-command-url","http://commands.yiffcraft.net/servers/mc_doridian_de.txt"));

			SSLUtils.nagIfNoSSL(plugin.playerHelper, ply);
		} else if(argStr.equalsIgnoreCase("writecommands")) {
			try {
				Hashtable<String, ICommand> commands = plugin.playerListener.commands;

				PrintWriter writer = new PrintWriter(new FileWriter("yb_commands.txt"));

				for(Map.Entry<String, ICommand> command : commands.entrySet()) {
					ICommand cmd = command.getValue();
					String help = cmd.getHelp();
					if(help.indexOf("\n") > 0) {
						help = help.substring(0, help.indexOf("\n"));
					}
					writer.println('/' + command.getKey() + '|' + cmd.getUsage() + " - " + help);
				}
				
				writer.close();
			}
			catch(Exception e) { throw new YiffBukkitCommandException(e); }
		}
	}
}
