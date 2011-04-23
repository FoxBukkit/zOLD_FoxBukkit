package de.doridian.yiffbukkit.commands;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("setspawn")
@Help("Moves the world spawn point to your current location.")
@Level(5)
public class SetSpawnCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {

		Location loc = ply.getLocation();
		ply.getWorld().setSpawnLocation(loc.getBlockX(),loc.getBlockY(),loc.getBlockZ());
		playerHelper.SendServerMessage(ply.getName() + " changed the world respawn point.");
	}
}
