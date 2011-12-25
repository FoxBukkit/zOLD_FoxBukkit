package de.doridian.yiffbukkit.commands;

import de.doridian.yiffbukkit.commands.ICommand.Help;
import de.doridian.yiffbukkit.commands.ICommand.Names;
import de.doridian.yiffbukkit.commands.ICommand.Permission;
import org.bukkit.entity.Player;

@Names("home")
@Help("Teleports you to your home position (see /sethome)")
@Permission("yiffbukkit.teleport.basic.home")
public class HomeCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) {
		if (plugin.jailEngine.isJailed(ply)) {
			playerHelper.sendDirectedMessage(ply, "You are jailed!");
			return;
		}

		plugin.playerHelper.teleportWithHistory(ply, playerHelper.getPlayerHomePosition(ply));
		playerHelper.sendServerMessage(ply.getName() + " went home!");
	}
}
