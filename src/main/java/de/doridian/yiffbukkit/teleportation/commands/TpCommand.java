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

		if(booleanFlags.contains('c')) {
			if(!sender.hasPermission("yiffbukkit.teleport.tp.coords"))
				throw new PermissionDeniedException();

			int x, y, z;
			if(args.length == 3) {
				x = Integer.valueOf(args[0]);
				y = Integer.valueOf(args[1]);
				z = Integer.valueOf(args[2]);
			} else if(args.length == 2) {
				x = Integer.valueOf(args[0]);
				z = Integer.valueOf(args[1]);
				y = sender.getWorld().getHighestBlockYAt(x, z) + 1;
			} else {
				throw new YiffBukkitCommandException("Wat?");
			}

			Location target = new Location(sender.getWorld(), x, y, z);
			plugin.playerHelper.teleportWithHistory(sender, target);

			return;
		}

		Player otherply = playerHelper.matchPlayerSingle(args[0]);

		String senderName = sender.getName();
		String otherName = otherply.getName();

		if (!playerHelper.canTp(sender, otherply))
			throw new PermissionDeniedException();

		if (booleanFlags.contains('s') && !sender.hasPermission("yiffbukkit.teleport.tp.silent"))
			throw new PermissionDeniedException();

		if (booleanFlags.contains('n')) {
			if (!sender.hasPermission("yiffbukkit.teleport.tp.near"))
				throw new PermissionDeniedException();

			final Location location = otherply.getLocation();
			final Vector vec = location.toVector().subtract(location.getDirection().multiply(3.0));

			location.setX(vec.getX());
			location.setY(vec.getY());
			location.setZ(vec.getZ());
			plugin.playerHelper.teleportWithHistory(sender, location);
		}
		else {
			plugin.playerHelper.teleportWithHistory(sender, otherply);
		}

		final List<Player> receivers = new ArrayList<Player>();

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
