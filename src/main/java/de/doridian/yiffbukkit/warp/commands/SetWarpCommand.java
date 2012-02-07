package de.doridian.yiffbukkit.warp.commands;

import de.doridian.yiffbukkit.main.commands.ICommand;
import de.doridian.yiffbukkit.warp.WarpDescriptor;
import de.doridian.yiffbukkitsplit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.ICommand.Usage;
import org.bukkit.entity.Player;

@Names("setwarp")
@Help("Creates a warp point with the specified name.")
@Usage("<warp point name>")
@Permission("yiffbukkitsplit.warp.setwarp")
public class SetWarpCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		if (argStr.isEmpty())
			throw new YiffBukkitCommandException("Not enough arguments");

		WarpDescriptor warp = plugin.warpEngine.setWarp(ply.getName(), argStr, ply.getLocation());
		playerHelper.sendDirectedMessage(ply, "Created warp \u00a79" + warp.name + "\u00a7f here. Use '/warp help' to see how to modify it.");
	}
}
