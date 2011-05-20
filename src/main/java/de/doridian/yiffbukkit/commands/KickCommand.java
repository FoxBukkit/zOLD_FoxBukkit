package de.doridian.yiffbukkit.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.PermissionDeniedException;
import de.doridian.yiffbukkit.util.PlayerFindException;
import de.doridian.yiffbukkit.util.Utils;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("kick")
@Help("Kicks specified user")
@Usage("<name> [reason here]")
@Level(2)
public class KickCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws PlayerFindException, PermissionDeniedException {
		Player otherply = playerHelper.MatchPlayerSingle(args[0]);

		String reason = commandSender.getName() + ": " + Utils.concatArray(args, 1, "Kicked");

		if(playerHelper.GetPlayerLevel(commandSender) < playerHelper.GetPlayerLevel(otherply))
			throw new PermissionDeniedException();

		otherply.kickPlayer(reason);
		//playerHelper.SendServerMessage(ply.getName() + " kicked " + otherply.getName() + " (reason: "+reason+")");
	}
}
