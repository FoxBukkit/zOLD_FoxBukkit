package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.commands.ICommand.*;

@Names({"unban", "pardon"})
@Help("Unbans specified user")
@Usage("<full name>")
@Level(3)
public class UnbanCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) {
		String otherply = args[0];
		if(!playerHelper.GetPlayerRank(otherply).equals("banned")) {
			playerHelper.SendDirectedMessage(ply, "Player is not banned!");
			return;
		}

		playerHelper.SetPlayerRank(otherply, "guest");
		playerHelper.SendServerMessage(ply.getName() + " unbanned " + otherply + "!");
	}
}
