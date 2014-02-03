package de.doridian.yiffbukkit.teleportation.commands;

import de.doridian.yiffbukkit.core.util.PlayerHelper;
import de.doridian.yiffbukkit.main.PermissionDeniedException;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.BooleanFlags;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

@Names("tp")
@Help(
		"Teleports you to the specified user.\n" +
		"Flags:\n" +
		"  -s teleports you silently.\n" +
		"  -n teleports you near the player.\n" +
		"  -c teleports you to coordinates."
)
@Usage("[<flags>] <name>")
@Permission("yiffbukkit.teleport.tp")
@BooleanFlags("snc")
public class TpCommand extends ICommand {
	@Override
	public void Run(Player sender, String[] args, String argStr) throws YiffBukkitCommandException {
		args = parseFlags(args);

		if (booleanFlags.contains('c')) {
			if (!sender.hasPermission("yiffbukkit.teleport.tp.coords"))
				throw new PermissionDeniedException();

			final int x, y, z;
			switch (args.length) {
			case 3:
				x = Integer.valueOf(args[0]);
				y = Integer.valueOf(args[1]);
				z = Integer.valueOf(args[2]);
				break;
			
			case 2:
				x = Integer.valueOf(args[0]);
				z = Integer.valueOf(args[1]);
				y = sender.getWorld().getHighestBlockYAt(x, z) + 1;
				break;
			
			default:
				throw new YiffBukkitCommandException("Wrong number of arguments.");
			}

			final Location target = new Location(sender.getWorld(), x, y, z);
			playerHelper.teleportWithHistory(sender, target);

			return;
		}

		final Player otherPlayer = playerHelper.matchPlayerSingle(args[0]);

		final String senderName = sender.getName();
		final String otherName = otherPlayer.getName();

		if (!playerHelper.canTp(sender, otherPlayer))
			throw new PermissionDeniedException();

		if (booleanFlags.contains('s') && !sender.hasPermission("yiffbukkit.teleport.tp.silent"))
			throw new PermissionDeniedException();

		if (booleanFlags.contains('n')) {
			if (!sender.hasPermission("yiffbukkit.teleport.tp.near"))
				throw new PermissionDeniedException();

			final Location location = otherPlayer.getLocation();
			location.subtract(location.getDirection().multiply(3.0));
			playerHelper.teleportWithHistory(sender, location);
		}
		else {
			playerHelper.teleportWithHistory(sender, otherPlayer);
		}

		final List<Player> receivers = new ArrayList<>();

		final boolean silentFlag = booleanFlags.contains('s');
		boolean silent = silentFlag;
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (!player.canSee(sender)) {
				silent = true;
				continue;
			}

			if (silentFlag && !player.hasPermission("yiffbukkit.teleport.tp.silent.see"))
				continue;

			receivers.add(player);
		}

		final String message;
		if (silent) {
			message = senderName + " silently teleported to " + otherName;
		}
		else {
			message = senderName + " teleported to " + otherName;
		}

		for (Player player : receivers) {
			PlayerHelper.sendDirectedMessage(player, message);
		}
	}
}
