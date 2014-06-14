/**
 * This file is part of FoxBukkit.
 *
 * FoxBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoxBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FoxBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.foxelbox.foxbukkit.main.commands;

import com.foxelbox.foxbukkit.core.util.PlayerHelper;
import com.foxelbox.foxbukkit.main.FoxBukkitCommandException;
import com.foxelbox.foxbukkit.main.commands.system.ICommand;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Help;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Names;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Permission;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Usage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.PriorityQueue;

@Names({"help", "?", "h"})
@Help("Prints a list of available commands or information about the specified command.")
@Usage("[<command>]")
@Permission("foxbukkit.help")
public class HelpCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr, String commandName) throws FoxBukkitCommandException {
		Map<String, ICommand> commands = plugin.commandSystem.getCommands();

		if(args.length > 0) {
			ICommand val = commands.get(args[0]);
			if (val == null || !val.canPlayerUseCommand(commandSender)) {
                if(commandSender instanceof Player) {
                    forwardCommandToRedis(commandSender, commandName, argStr);
                    return;
                }
                throw new FoxBukkitCommandException("Command not found!");
            }

			for (String line : val.getHelp().split("\n")) {
				PlayerHelper.sendDirectedMessage(commandSender, line);
			}
			PlayerHelper.sendDirectedMessage(commandSender, "Usage: /" + args[0] + " " + val.getUsage());
		}
		else {
			String ret = "Available commands: /";
			for (String key : new PriorityQueue<>(commands.keySet())) {
				if (key == "\u00a7")
					continue;

				ICommand val = commands.get(key);
				if (!val.canPlayerUseCommand(commandSender))
					continue;

				ret += key + ", /";
			}
			ret = ret.substring(0,ret.length() - 3);
			PlayerHelper.sendDirectedMessage(commandSender, ret);
            if(commandSender instanceof Player)
                forwardCommandToRedis(commandSender, commandName, argStr);
		}
	}
}
