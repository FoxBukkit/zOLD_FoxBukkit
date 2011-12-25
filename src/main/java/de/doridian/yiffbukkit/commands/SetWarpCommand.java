package de.doridian.yiffbukkit.commands;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.Help;
import de.doridian.yiffbukkit.commands.ICommand.Names;
import de.doridian.yiffbukkit.commands.ICommand.Permission;
import de.doridian.yiffbukkit.commands.ICommand.Usage;
import de.doridian.yiffbukkit.warp.WarpDescriptor;
import org.bukkit.entity.Player;

@Names("setwarp")
@Help("Creates a warp point with the specified name.")
@Usage("<warp point name>")
@Permission("yiffbukkit.warp.setwarp")
public class SetWarpCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		if (argStr.isEmpty())
			throw new YiffBukkitCommandException("Not enough arguments");

		WarpDescriptor warp = plugin.warpEngine.setWarp(ply.getName(), argStr, ply.getLocation());
		playerHelper.sendDirectedMessage(ply, "Created warp \u00a79" + warp.name + "\u00a7f here. Use '/warp help' to see how to modify it.");
	}
}
