package de.doridian.yiffbukkit.warp.commands;

import de.doridian.yiffbukkit.core.util.PlayerHelper;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Names("spawn")
@Help("Teleports you to the spawn position")
@Permission("yiffbukkit.teleport.basic.spawn")
public class SpawnCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr, String commandName) {
		if (playerHelper.isPlayerJailed(ply)) {
			PlayerHelper.sendDirectedMessage(ply, "You are jailed!");
			return;
		}

		Location location = playerHelper.getPlayerSpawnPosition(ply);
		plugin.playerHelper.teleportWithHistory(ply, location);
		PlayerHelper.sendDirectedMessage(ply, "Returned to the spawn!");
	}
}