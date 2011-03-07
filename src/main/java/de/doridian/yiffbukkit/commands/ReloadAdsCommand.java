package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkit;

public class ReloadAdsCommand extends ICommand {
	public int GetMinLevel() {
		return 3;
	}

	public ReloadAdsCommand(YiffBukkit plug) {
		super(plug);
	}

	public void Run(Player ply, String[] args, String argStr) {
		plugin.adHandler.ReloadAds();
	}

	public String GetHelp() {
		return "Reload ads";
	}
}
