package de.doridian.yiffbukkit.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("kickall")
@Help("Kicks everyone from the server except for yourself.")
@Usage("[<reason>]")
@Level(5)
@Permission("yiffbukkit.users.kickall")
public class KickAllCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) {
		if (argStr.isEmpty())
			argStr = "Clearing server.";

		for (Player player : plugin.getServer().getOnlinePlayers()) {
			if (player.equals(commandSender))
				continue;

			player.kickPlayer(argStr);
		}

		playerHelper.sendServerMessage(commandSender.getName() + " kicked everyone (reason: "+argStr+")");
	}
}
