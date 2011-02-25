package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkit;

public class VanishCommand extends ICommand {
	public VanishCommand(YiffBukkit plug) {
		super(plug);
	}

	@Override
	public int GetMinLevel() {
		return 3;
	}

	@Override
	public void Run(Player ply, String[] args, String argStr) {
		String playerName = ply.getName();
		if (plugin.playerHelper.vanishedPlayers.contains(playerName)) {
			plugin.playerHelper.vanishedPlayers.remove(playerName);
			plugin.playerHelper.SendDirectedMessage(ply, "Reappearing...");
		}
		else {
			plugin.playerHelper.vanishedPlayers.add(playerName);
			plugin.playerHelper.SendDirectedMessage(ply, "Vanishing...");
		}
		
	}

	@Override
	public String GetHelp() {
		return "Makes you invisible";
	}

	@Override
	public String GetUsage() {
		return "";
	}

}
