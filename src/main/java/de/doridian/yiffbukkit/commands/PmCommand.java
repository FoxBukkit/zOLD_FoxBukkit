package de.doridian.yiffbukkit.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.util.PlayerFindException;
import de.doridian.yiffbukkit.util.Utils;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names({"pm", "msg"})
@Help("Sends a private message to the specified user, that cannot be seen by anyone but the target and yourself.")
@Usage("<name> <text>")
@Level(0)
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

		playerHelper.sendDirectedMessage(commandSender, "§e[PM >] §f" + otherply.getName() + "§f: " + message);
		playerHelper.sendDirectedMessage(otherply, "§e[PM <] §f" + commandSender.getName() + "§f: " + message);
	}
}
