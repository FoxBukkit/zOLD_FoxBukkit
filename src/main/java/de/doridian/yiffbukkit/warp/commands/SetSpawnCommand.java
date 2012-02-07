package de.doridian.yiffbukkit.warp.commands;

import de.doridian.yiffbukkit.main.commands.ICommand;
import de.doridian.yiffbukkitsplit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.ICommand.Permission;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Names("setspawn")
@Help("Moves the world spawn point to your current location.")
@Permission("yiffbukkitsplit.setspawn")
public class SetSpawnCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {

		Location loc = ply.getLocation();
		ply.getWorld().setSpawnLocation(loc.getBlockX(),loc.getBlockY(),loc.getBlockZ());
		playerHelper.sendServerMessage(ply.getName() + " changed the world respawn point.");
	}
}
