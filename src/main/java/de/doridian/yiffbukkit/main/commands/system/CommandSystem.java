package de.doridian.yiffbukkit.main.commands.system;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.command.CommandSender;

import de.doridian.yiffbukkit.main.PermissionDeniedException;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Cost;
import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;

public class CommandSystem {
	private final YiffBukkit plugin;
	private final Map<String,ICommand> commands = new HashMap<String,ICommand>();

	public CommandSystem(YiffBukkit plugin) {
		this.plugin = plugin;
		plugin.commandSystem = this;
		scanCommands();
	}

	public void scanCommands() {
		commands.clear();
		scanCommands("de.doridian.yiffbukkit.advanced.commands");
		scanCommands("de.doridian.yiffbukkit.chat.commands");
		scanCommands("de.doridian.yiffbukkit.irc.commands");
		scanCommands("de.doridian.yiffbukkit.jail.commands");
		scanCommands("de.doridian.yiffbukkit.main.commands");
		scanCommands("de.doridian.yiffbukkit.mcbans.commands");
		scanCommands("de.doridian.yiffbukkit.permissions.commands");
		scanCommands("de.doridian.yiffbukkit.portal.commands");
		scanCommands("de.doridian.yiffbukkit.remote.commands");
		scanCommands("de.doridian.yiffbukkit.spawning.commands");
		//scanCommands("de.doridian.yiffbukkit.ssl.commands");
		scanCommands("de.doridian.yiffbukkit.teleportation.commands");
		scanCommands("de.doridian.yiffbukkit.transmute.commands");
		scanCommands("de.doridian.yiffbukkit.warp.commands");
		scanCommands("de.doridian.yiffbukkit.spectate.commands");
		scanCommands("de.doridian.yiffbukkit.yiffpoints.commands");
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
					plugin.bank.useFunds(playerName, price, "/"+cmd+" "+argStr);
					final double total = plugin.bank.getBalance(playerName);
					PlayerHelper.sendDirectedMessage(commandSender, "Used "+price+" YP from your account. You have "+total+" YP left.");
				}

				if(!(cmd.equals("msg") || cmd.equals("pm") || cmd.equals("conv") || cmd.equals("conversation")))
				{
					String logmsg = "YB Command: " + playerName + ": "  + cmd + " " + argStr;
					plugin.ircbot.sendToStaffChannel(logmsg);
					plugin.log(logmsg);
				}
				icmd.run(commandSender, args, argStr);
			}
			catch (YiffBukkitCommandException e) {
				PlayerHelper.sendDirectedMessage(commandSender,e.getMessage(), e.getColor());
			}
			catch (Exception e) {
				if (commandSender.hasPermission("yiffbukkit.detailederrors")) {
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
