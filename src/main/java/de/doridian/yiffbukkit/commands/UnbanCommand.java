package de.doridian.yiffbukkit.commands;

import org.bukkit.command.CommandSender;

import de.doridian.yiffbukkit.commands.ICommand.*;

@Names({"unban", "pardon"})
@Help("Unbans specified user")
@Usage("<full name>")
@Level(3)
public class UnbanCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) {
		String otherply = args[0];
		plugin.mcbans.unban(commandSender, otherply);
		if(plugin.playerHelper.getPlayerRank(otherply).equalsIgnoreCase("banned")) plugin.playerHelper.setPlayerRank(otherply, "guest");
	}
}
