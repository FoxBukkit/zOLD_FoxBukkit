package de.doridian.yiffbukkit.mcbans.commands;

import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.*;
import org.bukkit.command.CommandSender;

@Names({"unban", "pardon"})
@Help("Unbans specified user")
@Usage("<full name>")
@Permission("yiffbukkit.users.unban")
@AbusePotential
public class UnbanCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) {
		plugin.mcbans.unban(commandSender, args[0]);
	}
}
