package de.doridian.yiffbukkit.commands;

import de.doridian.yiffbukkit.PermissionDeniedException;
import de.doridian.yiffbukkit.commands.ICommand.Help;
import de.doridian.yiffbukkit.commands.ICommand.Names;
import de.doridian.yiffbukkit.commands.ICommand.Permission;
import de.doridian.yiffbukkit.commands.ICommand.Usage;
import de.doridian.yiffbukkit.util.PlayerFindException;
import de.doridian.yiffbukkit.util.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Names("kick")
@Help("Kicks specified user")
@Usage("<name> [reason here]")
@Permission("yiffbukkit.users.kick")
public class KickCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws PlayerFindException, PermissionDeniedException {
		Player otherply = playerHelper.matchPlayerSingle(args[0]);

		if(playerHelper.getPlayerLevel(commandSender) < playerHelper.getPlayerLevel(otherply))
			throw new PermissionDeniedException();

		String reason = commandSender.getName() + ": " + Utils.concatArray(args, 1, "Kicked");

		otherply.kickPlayer(reason);
		//playerHelper.SendServerMessage(ply.getName() + " kicked " + otherply.getName() + " (reason: "+reason+")");
	}
}
