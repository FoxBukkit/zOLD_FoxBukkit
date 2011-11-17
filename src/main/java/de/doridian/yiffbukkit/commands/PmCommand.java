package de.doridian.yiffbukkit.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.util.PlayerFindException;
import de.doridian.yiffbukkit.util.Utils;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names({"pm", "msg"})
@Help("Sends a private message to the specified user, that cannot be seen by anyone but the target and yourself.")
@Usage("<name> <text>")
@Permission("yiffbukkit.communication.pm")
public class PmCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws PlayerFindException {
		if(args.length < 1){
			playerHelper.sendDirectedMessage(commandSender, "Usage: /pm " + getUsage());
			return;
		}

		Player otherply = playerHelper.matchPlayerSingle(args[0]);

		String message = Utils.concatArray(args, 1, "");

		playerHelper.sendDirectedMessage(commandSender, "\u00a7e[PM >] \u00a7f" + otherply.getName() + "\u00a7f: " + message);
		playerHelper.sendDirectedMessage(otherply, "\u00a7e[PM <] \u00a7f" + commandSender.getName() + "\u00a7f: " + message);
	}
}
