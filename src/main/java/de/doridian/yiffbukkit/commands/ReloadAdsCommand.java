package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkitPlayerListener;

public class ReloadAdsCommand extends ICommand {
	public int GetMinLevel() {
		return 3;
	}

	public ReloadAdsCommand(YiffBukkitPlayerListener playerListener) {
		super(playerListener);
	}

	public void Run(Player ply, String[] args, String argStr) {
		plugin.adHandler.ReloadAds();
	}

	public String GetHelp() {
		return "Reload ads";
	}
}
