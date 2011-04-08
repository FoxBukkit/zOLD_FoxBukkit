package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkitPlayerListener;

public class KickAllCommand extends ICommand {

	public KickAllCommand(YiffBukkitPlayerListener playerListener) {
		super(playerListener);
	}

	@Override
	public int GetMinLevel() {
		return 5;
	}

	@Override
	public void Run(Player ply, String[] args, String argStr) {
		if (argStr.isEmpty())
			argStr = "Clearing server.";

		for (Player player : plugin.getServer().getOnlinePlayers()) {
			if (player.equals(ply))
				continue;

			player.kickPlayer(argStr);
		}

		playerHelper.SendServerMessage(ply.getName() + " kicked everyone (reason: "+argStr+")");
	}

	@Override
	public String GetHelp() {
		return "Kicks everyone from the server except for yourself.";
	}

	@Override
	public String GetUsage() {
		return "[<reason>]";
	}


}
