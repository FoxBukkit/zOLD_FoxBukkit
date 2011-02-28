package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.warp.WarpDescriptor;
import de.doridian.yiffbukkit.warp.WarpException;

public class SetWarpCommand extends ICommand {
	
	@Override
	public int GetMinLevel() {
		return 3;
	}

	public SetWarpCommand(YiffBukkit plug) {
		super(plug);
	}

	@Override
	public void Run(Player ply, String[] args, String argStr) {
		try {
			// TODO: error for argStr==""
			WarpDescriptor warp = plugin.warpEngine.setWarp(ply.getName(), argStr, ply.getLocation());
			playerHelper.SendDirectedMessage(ply, "Created warp §9" + warp.name + "§f here. Use /cwarp to modify it.");
		}
		catch (WarpException e) {
			playerHelper.SendDirectedMessage(ply, e.getMessage());
		}
		catch (ArrayIndexOutOfBoundsException e) {
			playerHelper.SendDirectedMessage(ply, "Not enough arguments.");
		}
	}

	@Override
	public String GetHelp() {
		return "Creates a warp point with the specified name.";
	}

	@Override
	public String GetUsage() {
		return "<warp point name>";
	}
}
