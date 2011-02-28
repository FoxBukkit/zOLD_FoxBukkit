package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.warp.WarpException;

public class WarpCommand extends ICommand {
	
	@Override
	public int GetMinLevel() {
		return 0;
	}

	public WarpCommand(YiffBukkit plug) {
		super(plug);
	}

	@Override
	public void Run(Player ply, String[] args, String argStr) {
		try {
			// TODO: error for argStr==""
			ply.teleportTo(plugin.warpEngine.getWarp(ply.getName(), args[0]).location);
		}
		catch (WarpException e) {
			playerHelper.SendDirectedMessage(ply, e.getMessage());
		}
	}

	@Override
	public String GetHelp() {
		return "Teleports you to the specified warp point.";
	}

	@Override
	public String GetUsage() {
		return "<warp point name>";
	}
}
