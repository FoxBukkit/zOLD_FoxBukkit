package de.doridian.yiffbukkit.commands;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkit;

public class SpawnCommand extends ICommand {
	public int GetMinLevel() {
		return 0;
	}

	public SpawnCommand(YiffBukkit plug) {
		super(plug);
	}

	public void Run(Player ply, String[] args, String argStr) {
		if (plugin.jailEngine.isJailed(ply)) {
			playerHelper.SendDirectedMessage(ply, "You are jailed!");
			return;
		}

		Location location = ply.getWorld().getSpawnLocation();
		location.setX(location.getX()+0.5);
		location.setZ(location.getZ()+0.5);
		ply.teleport(location);
		playerHelper.SendServerMessage(ply.getName() + " returned to the spawn!");
	}

	public String GetHelp() {
		return "Teleports you to the spawn position";
	}
}