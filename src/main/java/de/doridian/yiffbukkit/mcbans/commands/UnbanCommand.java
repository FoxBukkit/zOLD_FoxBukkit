package de.doridian.yiffbukkit.mcbans.commands;

import de.doridian.yiffbukkit.main.commands.ICommand;
import de.doridian.yiffbukkit.main.commands.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.ICommand.Usage;
import org.bukkit.command.CommandSender;

@Names({"unban", "pardon"})
@Help("Unbans specified user")
@Usage("<full name>")
@Permission("yiffbukkitsplit.users.unban")
public class UnbanCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) {
		plugin.mcbans.unban(commandSender, args[0]);
	}
}
