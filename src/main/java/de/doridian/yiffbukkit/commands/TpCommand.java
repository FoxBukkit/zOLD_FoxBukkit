package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.PermissionDeniedException;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("tp")
@Help("Teleports you to the specified user. -s teleports you silently.")
@Usage("[-s] <name>")
@Permission("yiffbukkit.teleport.tp")
@BooleanFlags("s")
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

		ply.teleport(otherply);

		if (plugin.vanish.vanishedPlayers.contains(playerName) || booleanFlags.contains('s')) {
			playerHelper.sendServerMessage(playerName + " silently teleported to " + otherName, "yiffbukkit.vanish.see");
		}
		else {
			playerHelper.sendServerMessage(playerName + " teleported to " + otherName);
		}
	}
}
