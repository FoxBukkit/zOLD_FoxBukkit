package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkit;

public class HomeCommand extends ICommand {
	public int GetMinLevel() {
		return 0;
	}

	public HomeCommand(YiffBukkit plug) {
		super(plug);
	}

	public void Run(Player ply, String[] args, String argStr) {
		ply.teleportTo(playerHelper.GetPlayerHomePosition(ply));
		playerHelper.SendServerMessage(ply.getName() + " went home!");
	}

	public String GetHelp() {
		return "Teleports you to your home position (see /sethome)";
	}

	public String GetUsage() {
		return "";
	}
}