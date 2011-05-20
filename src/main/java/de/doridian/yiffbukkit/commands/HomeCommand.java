package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("home")
@Help("Teleports you to your home position (see /sethome)")
@Level(0)
public class HomeCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) {
		if (plugin.jailEngine.isJailed(ply)) {
			playerHelper.SendDirectedMessage(ply, "You are jailed!");
			return;
		}

		ply.teleport(playerHelper.GetPlayerHomePosition(ply));
		playerHelper.SendServerMessage(ply.getName() + " went home!");
	}
}
