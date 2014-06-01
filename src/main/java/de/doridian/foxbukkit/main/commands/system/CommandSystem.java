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
package de.doridian.foxbukkit.main.commands.system;

import de.doridian.foxbukkit.chat.commands.ForwardToRedisCommand;
import de.doridian.foxbukkit.core.FoxBukkit;
import de.doridian.foxbukkit.core.util.PlayerHelper;
import de.doridian.foxbukkit.main.FoxBukkitCommandException;
import de.doridian.foxbukkit.main.PermissionDeniedException;
import de.doridian.foxbukkit.main.commands.system.ICommand.Cost;
import de.doridian.foxbukkit.main.util.Utils;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class CommandSystem {
	private final FoxBukkit plugin;
	private final Map<String,ICommand> commands = new HashMap<>();

	public CommandSystem(FoxBukkit plugin) {
		this.plugin = plugin;
		plugin.commandSystem = this;
		scanCommands();
	}

	public void scanCommands() {
		commands.clear();
		scanCommands("de.doridian.foxbukkit.chat.commands");
		scanCommands("de.doridian.foxbukkit.irc.commands");
		scanCommands("de.doridian.foxbukkit.main.commands");
		scanCommands("de.doridian.foxbukkit.bans.commands");
		scanCommands("de.doridian.foxbukkit.permissions.commands");
		scanCommands("de.doridian.foxbukkit.portal.commands");
		scanCommands("de.doridian.foxbukkit.remote.commands");
		scanCommands("de.doridian.foxbukkit.spawning.commands");
		//scanCommands("de.doridian.foxbukkit.ssl.commands");
		scanCommands("de.doridian.foxbukkit.teleportation.commands");
		scanCommands("de.doridian.foxbukkit.transmute.commands");
		scanCommands("de.doridian.foxbukkit.warp.commands");
		scanCommands("de.doridian.foxbukkit.spectate.commands");
		scanCommands("de.doridian.foxbukkit.foxpoints.commands");
	}

	public void scanCommands(String packageName) {
		for (Class<? extends ICommand> commandClass : Utils.getSubClasses(ICommand.class, packageName)) {
			try {
				commandClass.newInstance();
			}
			catch (InstantiationException e) {
				// We try to instantiate an interface
				// or an object that does not have a
				// default constructor
				continue;
			}
			catch (IllegalAccessException e) {
				// The class/ctor is not public
				continue;
			}
			catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}

	public void registerCommand(String name, ICommand command) {
		commands.put(name, command);
	}

	public Map<String,ICommand> getCommands() {
		return commands;
	}

	public boolean runCommand(CommandSender commandSender, String cmd, String argStr) {
		String args[];
		if(argStr.isEmpty()) {
			args = new String[0];
		} else {
			args = argStr.split(" +");
		}
		return runCommand(commandSender, cmd, args, argStr);
	}

	public boolean runCommand(CommandSender commandSender, String cmd, String[] args, String argStr) {
		if (commands.containsKey(cmd)) {
			final String playerName = commandSender.getName();
			final ICommand icmd = commands.get(cmd);
			try {
				if(!icmd.canPlayerUseCommand(commandSender)) {
					Cost costAnnotation = icmd.getClass().getAnnotation(Cost.class);
					if (costAnnotation == null)
						throw new PermissionDeniedException();

					final double price = costAnnotation.value();
					plugin.bank.useFunds(commandSender.getUniqueId(), price, "/"+cmd+" "+argStr);
					final double total = plugin.bank.getBalance(commandSender.getUniqueId());
					PlayerHelper.sendDirectedMessage(commandSender, "Used "+price+" YP from your account. You have "+total+" YP left.");
				}

				if(needsLogging(commandSender, icmd))
				{
					String logmsg = "FB Command: " + playerName + ": "  + cmd + " " + argStr;
					plugin.log(logmsg);
				}
				icmd.run(commandSender, args, argStr, cmd);
			}
			catch (PermissionDeniedException e) {
				String logmsg = "FB Command denied: " + playerName + ": "  + cmd + " " + argStr;
				plugin.log(logmsg);

				PlayerHelper.sendDirectedMessage(commandSender, e.getMessage(), e.getColor());
			}
			catch (FoxBukkitCommandException e) {
				PlayerHelper.sendDirectedMessage(commandSender, e.getMessage(), e.getColor());
			}
			catch (Exception e) {
				if (commandSender.hasPermission("foxbukkit.detailederrors")) {
					PlayerHelper.sendDirectedMessage(commandSender,"Command error: "+e+" in "+e.getStackTrace()[0]);
					e.printStackTrace();
				}
				else {
					PlayerHelper.sendDirectedMessage(commandSender,"Command error!");
				}
			}
			return true;
		}
		return false;
	}

	private boolean needsLogging(CommandSender commandSender, ICommand command) {
		final Class<? extends ICommand> cls = command.getClass();
		if (cls == ForwardToRedisCommand.class)
			return false;

		if (commandSender instanceof BlockCommandSender)
			return command.hasAbusePotential();

		return true;
	}

	public boolean runCommand(CommandSender commandSender, String baseCmd) {
		int posSpace = baseCmd.indexOf(' ');
		String cmd; String argStr;
		if (posSpace < 0) {
			cmd = baseCmd.toLowerCase();
			argStr = "";
		} else {
			cmd = baseCmd.substring(0, posSpace).trim().toLowerCase();
			argStr = baseCmd.substring(posSpace).trim();
		}
		return runCommand(commandSender, cmd, argStr);
	}
}
