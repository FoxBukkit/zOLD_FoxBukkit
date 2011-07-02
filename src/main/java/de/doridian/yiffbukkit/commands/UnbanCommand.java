package de.doridian.yiffbukkit.commands;

import org.bukkit.command.CommandSender;

import de.doridian.yiffbukkit.commands.ICommand.*;

@Names({"unban", "pardon"})
@Help("Unbans specified user")
@Usage("<full name>")
@Level(3)
@Permission("yiffbukkit.users.unban")
public class UnbanCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) {
		plugin.mcbans.unban(commandSender, args[0]);
	}
}
