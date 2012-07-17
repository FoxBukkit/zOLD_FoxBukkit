package de.doridian.yiffbukkit.warp.commands;

import de.doridian.yiffbukkit.main.commands.ICommand;
import de.doridian.yiffbukkit.main.commands.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.ICommand.Permission;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;

import org.bukkit.Location;
import org.bukkit.entity.Player;

@Names("spawn")
@Help("Teleports you to the spawn position")
@Permission("yiffbukkit.teleport.basic.spawn")
public class SpawnCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) {
		if (plugin.jailEngine.isJailed(ply)) {
			PlayerHelper.sendDirectedMessage(ply, "You are jailed!");
			return;
		}

		Location location = playerHelper.getPlayerSpawnPosition(ply);
		plugin.playerHelper.teleportWithHistory(ply, location);
		playerHelper.sendServerMessage(ply.getName() + " returned to the spawn!");
	}
}