package de.doridian.yiffbukkit.teleportation.commands;

import java.util.ArrayList;
import java.util.List;
import de.doridian.yiffbukkit.main.PermissionDeniedException;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.BooleanFlags;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@Names("tp")
@Help(
		"Teleports you to the specified user.\n" +
		"Flags:\n" +
		"  -s teleports you silently.\n" +
		"  -n teleports you near the player."
)
@Usage("[<flags>] <name>")
@Permission("yiffbukkit.teleport.tp")
@BooleanFlags("sn")
public class TpCommand extends ICommand {
	@Override
	public void Run(Player sender, String[] args, String argStr) throws YiffBukkitCommandException {
		args = parseFlags(args);
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

			if (silentFlag && !sender.hasPermission("yiffbukkit.teleport.tp.silent.see"))
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
