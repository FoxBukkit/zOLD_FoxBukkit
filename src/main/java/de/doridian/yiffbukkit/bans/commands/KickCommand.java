package de.doridian.yiffbukkit.bans.commands;

import de.doridian.yiffbukkit.main.PermissionDeniedException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;
import de.doridian.yiffbukkit.main.util.PlayerFindException;
import de.doridian.yiffbukkit.main.util.Utils;
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

		otherply.kickPlayer("kick|" + reason);
		//playerHelper.SendServerMessage(ply.getName() + " kicked " + otherply.getName() + " (reason: "+reason+")");
	}
}
