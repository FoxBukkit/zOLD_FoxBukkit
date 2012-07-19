package de.doridian.yiffbukkit.warp.commands;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.ICommand;
import de.doridian.yiffbukkit.warp.WarpDescriptor;
import de.doridian.yiffbukkit.main.commands.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.ICommand.Usage;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

@Names("createwarp")
@Help("Creates a warp point at the guest spawn with the specified name for the specified player.")
@Usage("<warp point name> <exact owner name>")
@Permission("yiffbukkit.warp.createwarp")
public class CreateWarpCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		if (argStr.isEmpty())
			throw new YiffBukkitCommandException("Not enough arguments");

		final String warpName = args[0];
		final String ownerName = args[1];
		final Location location = plugin.warpEngine.getWarp(null, "guest_spawn").location;

		WarpDescriptor warp = plugin.warpEngine.setWarp(ownerName, warpName, location);
		PlayerHelper.sendDirectedMessage(commandSender, "Created warp \u00a79" + warp.name + "\u00a7f here. Use '/warp help' to see how to modify it.");
	}
}
