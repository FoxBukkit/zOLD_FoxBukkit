package de.doridian.yiffbukkit.commands;

import de.doridian.yiffbukkit.PermissionDeniedException;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.BooleanFlags;
import de.doridian.yiffbukkit.commands.ICommand.Help;
import de.doridian.yiffbukkit.commands.ICommand.Names;
import de.doridian.yiffbukkit.commands.ICommand.Permission;
import de.doridian.yiffbukkit.commands.ICommand.Usage;
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
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		args = parseFlags(args);
		Player otherply = playerHelper.matchPlayerSingle(args[0]);

		String playerName = ply.getName();
		String otherName = otherply.getName();

		if (!playerHelper.canTp(ply, otherply))
			throw new PermissionDeniedException();

		if (booleanFlags.contains('s') && !plugin.permissionHandler.has(ply, "yiffbukkit.vanish"))
			throw new PermissionDeniedException();

		if (booleanFlags.contains('n')) {
			if (!plugin.permissionHandler.has(ply, "yiffbukkit.vanish"))
				throw new PermissionDeniedException();

			final Location location = otherply.getLocation();
			final Vector vec = location.toVector().subtract(location.getDirection().multiply(3.0));

			location.setX(vec.getX());
			location.setY(vec.getY());
			location.setZ(vec.getZ());
			plugin.playerHelper.teleportWithHistory(ply, location);
		}
		else {
			plugin.playerHelper.teleportWithHistory(ply, otherply);
		}

		if (plugin.vanish.isVanished(ply) || booleanFlags.contains('s')) {
			playerHelper.sendServerMessage(playerName + " silently teleported to " + otherName, "yiffbukkit.vanish.see");
		}
		else {
			playerHelper.sendServerMessage(playerName + " teleported to " + otherName);
		}
	}
}
