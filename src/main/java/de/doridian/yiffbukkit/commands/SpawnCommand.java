package de.doridian.yiffbukkit.commands;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("spawn")
@Help("Teleports you to the spawn position")
@Permission("yiffbukkit.teleport.basic.spawn")
public class SpawnCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) {
		if (plugin.jailEngine.isJailed(ply)) {
			playerHelper.sendDirectedMessage(ply, "You are jailed!");
			return;
		}

		Location location = playerHelper.getPlayerSpawnPosition(ply);
		ply.teleport(location);
		playerHelper.sendServerMessage(ply.getName() + " returned to the spawn!");
	}
}