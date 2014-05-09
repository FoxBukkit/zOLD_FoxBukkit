package de.doridian.yiffbukkit.warp.commands;

import de.doridian.yiffbukkit.core.util.PlayerHelper;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Cost;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;
import de.doridian.yiffbukkit.warp.WarpDescriptor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.UUID;

@Names("setwarp")
@Help("Creates a warp point with the specified name, for the specified player or yourself. When run from the console, the warp is created at the guest spawn.")
@Usage("<warp point name> [<exact owner name>]")
@Permission("yiffbukkit.warp.setwarp")
@Cost(300)
public class SetWarpCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		final String warpName;
		final UUID ownerName;

		switch (args.length) {
		case 0:
			throw new YiffBukkitCommandException("Not enough arguments");

		case 1:
			warpName = args[0];
			ownerName = asPlayer(commandSender).getUniqueId();
			break;

		default:
			warpName = args[0];
			ownerName = playerHelper.matchPlayerSingle(args[1]).getUniqueId();
		}

		WarpDescriptor warp = plugin.warpEngine.setWarp(ownerName, warpName, getWarpTargetLocation(commandSender));
		PlayerHelper.sendDirectedMessage(commandSender, "Created warp \u00a79" + warp.name + "\u00a7f. Use '/warp help' to see how to modify it.");
	}

	private Location getWarpTargetLocation(CommandSender commandSender) {
		final Location guestSpawn = playerHelper.getRankSpawnPosition(plugin.getOrCreateWorld("world", World.Environment.NORMAL), "guest");
		return getCommandSenderLocation(commandSender, false, guestSpawn);
	}
}
