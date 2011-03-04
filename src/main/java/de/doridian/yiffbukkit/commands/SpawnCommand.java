package de.doridian.yiffbukkit.commands;

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

		ply.teleportTo(ply.getWorld().getSpawnLocation());
		playerHelper.SendServerMessage(ply.getName() + " returned to the spawn!");
	}

	public String GetHelp() {
		return "Teleports you to the spawn position";
	}

	public String GetUsage() {
		return "";
	}
}