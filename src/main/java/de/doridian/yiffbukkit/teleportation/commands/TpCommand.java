package de.doridian.yiffbukkit.teleportation.commands;

import de.doridian.yiffbukkit.main.commands.ICommand;
import de.doridian.yiffbukkitsplit.PermissionDeniedException;
import de.doridian.yiffbukkitsplit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.ICommand.BooleanFlags;
import de.doridian.yiffbukkit.main.commands.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.ICommand.Usage;
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
@Permission("yiffbukkitsplit.teleport.tp")
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

		if (booleanFlags.contains('s') && !plugin.permissionHandler.has(ply, "yiffbukkitsplit.vanish"))
			throw new PermissionDeniedException();

		if (booleanFlags.contains('n')) {
			if (!plugin.permissionHandler.has(ply, "yiffbukkitsplit.vanish"))
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
			playerHelper.sendServerMessage(playerName + " silently teleported to " + otherName, "yiffbukkitsplit.vanish.see");
		}
		else {
			playerHelper.sendServerMessage(playerName + " teleported to " + otherName);
		}
	}
}
