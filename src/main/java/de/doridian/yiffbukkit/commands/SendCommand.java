package de.doridian.yiffbukkit.commands;

import de.doridian.yiffbukkit.PermissionDeniedException;
import de.doridian.yiffbukkit.commands.ICommand.Help;
import de.doridian.yiffbukkit.commands.ICommand.Names;
import de.doridian.yiffbukkit.commands.ICommand.Permission;
import de.doridian.yiffbukkit.commands.ICommand.Usage;
import de.doridian.yiffbukkit.util.PlayerFindException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Names("send")
@Help("Teleports the specified source user to the specified target user.")
@Usage("<source> <target>")
@Permission("yiffbukkit.teleport.send")
public class SendCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws PlayerFindException, PermissionDeniedException {
		Player fromPlayer = playerHelper.matchPlayerSingle(args[0]);

		Player toPlayer = playerHelper.matchPlayerSingle(args[1]);

		if (!playerHelper.canSummon(commandSender, fromPlayer))
			throw new PermissionDeniedException();

		if (!playerHelper.canTp(commandSender, toPlayer))
			throw new PermissionDeniedException();

		plugin.playerHelper.teleportWithHistory(fromPlayer, toPlayer);

		playerHelper.sendServerMessage(commandSender.getName() + " sent " + fromPlayer.getName() + " to " + toPlayer.getName());
	}
}
