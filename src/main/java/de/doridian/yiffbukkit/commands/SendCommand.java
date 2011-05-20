package de.doridian.yiffbukkit.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.PermissionDeniedException;
import de.doridian.yiffbukkit.util.PlayerFindException;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("send")
@Help("Teleports the specified source user to the specified target user.")
@Usage("<source> <target>")
@Level(3)
public class SendCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws PlayerFindException, PermissionDeniedException {
		Player fromPlayer = playerHelper.MatchPlayerSingle(args[0]);

		Player toPlayer = playerHelper.MatchPlayerSingle(args[1]);

		if (!playerHelper.CanSummon(commandSender, fromPlayer))
			throw new PermissionDeniedException();

		if (!playerHelper.CanTp(commandSender, toPlayer))
			throw new PermissionDeniedException();

		fromPlayer.teleport(toPlayer);

		playerHelper.SendServerMessage(commandSender.getName() + " sent " + fromPlayer.getName() + " to " + toPlayer.getName());
	}
}
