package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.warp.WarpDescriptor;
import de.doridian.yiffbukkit.warp.WarpException;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("setwarp")
@Help("Creates a warp point with the specified name.")
@Usage("<warp point name>")
@Level(3)
@Permission("yiffbukkit.warp.setwarp")
public class SetWarpCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws WarpException {
		try {
			// TODO: error for argStr==""
			WarpDescriptor warp = plugin.warpEngine.setWarp(ply.getName(), argStr, ply.getLocation());
			playerHelper.sendDirectedMessage(ply, "Created warp §9" + warp.name + "§f here. Use '/warp help' to see how to modify it.");
		}
		catch (ArrayIndexOutOfBoundsException e) {
			playerHelper.sendDirectedMessage(ply, "Not enough arguments.");
		}
	}
}
