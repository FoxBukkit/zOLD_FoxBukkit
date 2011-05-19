package de.doridian.yiffbukkit.commands;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("spawn")
@Help("Teleports you to the spawn position")
@Level(0)
public class SpawnCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) {
		if (plugin.jailEngine.isJailed(ply)) {
			playerHelper.SendDirectedMessage(ply, "You are jailed!");
			return;
		}

		Location location = playerHelper.getPlayerSpawnPosition(ply);
		location.setX(location.getX()+0.5);
		location.setZ(location.getZ()+0.5);
		ply.teleport(location);
		playerHelper.SendServerMessage(ply.getName() + " returned to the spawn!");
	}
}