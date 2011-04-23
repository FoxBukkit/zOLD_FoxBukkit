package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("kickall")
@Help("Kicks everyone from the server except for yourself.")
@Usage("[<reason>]")
@Level(5)
public class KickAllCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) {
		if (argStr.isEmpty())
			argStr = "Clearing server.";

		for (Player player : plugin.getServer().getOnlinePlayers()) {
			if (player.equals(ply))
				continue;

			player.kickPlayer(argStr);
		}

		playerHelper.SendServerMessage(ply.getName() + " kicked everyone (reason: "+argStr+")");
	}
}
