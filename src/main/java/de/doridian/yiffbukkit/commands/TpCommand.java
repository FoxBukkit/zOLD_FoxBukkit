package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.PermissionDeniedException;
import de.doridian.yiffbukkit.util.PlayerFindException;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("tp")
@Help("Teleports you to the specified user")
@Usage("<name>")
@Permission("yiffbukkit.teleport.tp")
public class TpCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws PlayerFindException, PermissionDeniedException {
		Player otherply = playerHelper.matchPlayerSingle(args[0]);

		String playerName = ply.getName();
		String otherName = otherply.getName();

		if (!playerHelper.canTp(ply, otherply))
			throw new PermissionDeniedException();

		ply.teleport(otherply);

		if (plugin.vanish.vanishedPlayers.contains(playerName)) {
			playerHelper.sendServerMessage(playerName + " silently teleported to " + otherName, "yiffbukkit.vanish");
		}
		else {
			playerHelper.sendServerMessage(playerName + " teleported to " + otherName);
		}
	}
}
