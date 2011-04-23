package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("reloadads")
@Help("Reload ads")
@Level(3)
public class ReloadAdsCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) {
		plugin.adHandler.ReloadAds();
	}
}
