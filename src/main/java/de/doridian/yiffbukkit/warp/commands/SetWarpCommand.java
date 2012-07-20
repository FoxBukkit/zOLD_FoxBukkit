package de.doridian.yiffbukkit.warp.commands;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.ICommand;
import de.doridian.yiffbukkit.warp.WarpDescriptor;
import de.doridian.yiffbukkit.warp.WarpException;
import de.doridian.yiffbukkit.main.commands.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.ICommand.Usage;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Names("setwarp")
@Help("Creates a warp point with the specified name, for the specified player or yourself. When run from the console, the warp is created at the guest spawn.")
@Usage("<warp point name> [<exact owner name>]")
@Permission("yiffbukkit.warp.setwarp")
public class SetWarpCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		final String warpName;
		final String ownerName;

		switch (args.length) {
		case 0:
			throw new YiffBukkitCommandException("Not enough arguments");

		case 1:
			warpName = args[0];
			ownerName = asPlayer(commandSender).getName();
			break;

		default:
			warpName = args[0];
			ownerName = args[1];
		}

		WarpDescriptor warp = plugin.warpEngine.setWarp(ownerName, warpName, getWarpTargetLocation(commandSender));
		PlayerHelper.sendDirectedMessage(commandSender, "Created warp \u00a79" + warp.name + "\u00a7f. Use '/warp help' to see how to modify it.");
	}

	private Location getWarpTargetLocation(CommandSender commandSender) throws WarpException {
		if (commandSender instanceof Player)
			return ((Player) commandSender).getLocation();

		return plugin.warpEngine.getWarp(null, "guest_spawn").location;
	}
}
