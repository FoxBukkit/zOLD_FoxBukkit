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
		if(!playerHelper.GetPlayerRank(otherply).equals("banned")) {
			playerHelper.SendDirectedMessage(commandSender, "Player is not banned!");
			return;
		}

		playerHelper.SetPlayerRank(otherply, "guest");
		playerHelper.SendServerMessage(commandSender.getName() + " unbanned " + otherply + "!");
	}
}
