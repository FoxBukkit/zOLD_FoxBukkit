package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.warp.WarpDescriptor;
import de.doridian.yiffbukkit.warp.WarpException;

public class RemoveWarpCommand extends ICommand {
	
	@Override
	public int GetMinLevel() {
		return 0;
	}

	public RemoveWarpCommand(YiffBukkit plug) {
		super(plug);
	}

	@Override
	public void Run(Player ply, String[] args, String argStr) {
		try {
			// TODO: error for argStr==""
			WarpDescriptor warp = plugin.warpEngine.removeWarp(ply.getName(), argStr);
			playerHelper.SendDirectedMessage(ply, "Removed warp §9" + warp.name + "§f.");
		}
		catch (WarpException e) {
			playerHelper.SendDirectedMessage(ply, e.getMessage());
		}
	}

	@Override
	public String GetHelp() {
		return "Removes the specified warp point.";
	}

	@Override
	public String GetUsage() {
		return "<warp point name>";
	}
}
