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
		if (playerHelper.vanishedPlayers.contains(playerName)) {
			playerHelper.vanishedPlayers.remove(playerName);
			playerHelper.SendServerMessage(ply.getName() + " reappeared.", playerHelper.GetPlayerLevel(ply));
		}
		else {
			playerHelper.vanishedPlayers.add(playerName);
			playerHelper.SendServerMessage(ply.getName() + " vanished.", playerHelper.GetPlayerLevel(ply));
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
