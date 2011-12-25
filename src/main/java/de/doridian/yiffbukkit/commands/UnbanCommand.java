package de.doridian.yiffbukkit.commands;

import de.doridian.yiffbukkit.commands.ICommand.Help;
import de.doridian.yiffbukkit.commands.ICommand.Names;
import de.doridian.yiffbukkit.commands.ICommand.Permission;
import de.doridian.yiffbukkit.commands.ICommand.Usage;
import org.bukkit.command.CommandSender;

@Names({"unban", "pardon"})
@Help("Unbans specified user")
@Usage("<full name>")
@Permission("yiffbukkit.users.unban")
public class UnbanCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) {
		plugin.mcbans.unban(commandSender, args[0]);
	}
}
