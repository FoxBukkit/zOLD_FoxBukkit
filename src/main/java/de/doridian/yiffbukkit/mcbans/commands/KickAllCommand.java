package de.doridian.yiffbukkit.mcbans.commands;

import de.doridian.yiffbukkit.main.commands.ICommand;
import de.doridian.yiffbukkit.main.commands.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.ICommand.Usage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Names("kickall")
@Help("Kicks everyone from the server except for yourself.")
@Usage("[<reason>]")
@Permission("yiffbukkitsplit.users.kickall")
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
