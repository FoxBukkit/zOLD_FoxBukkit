package de.doridian.yiffbukkit.warp.commands;

import de.doridian.yiffbukkit.core.util.PlayerHelper;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Names("setspawn")
@Help("Moves the world spawn point to your current location.")
@Permission("yiffbukkit.setspawn")
public class SetSpawnCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {

		Location loc = ply.getLocation();
		ply.getWorld().setSpawnLocation(loc.getBlockX(),loc.getBlockY(),loc.getBlockZ());
		PlayerHelper.sendServerMessage(ply.getName() + " changed the world respawn point.");
	}
}
