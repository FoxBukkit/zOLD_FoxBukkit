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
public class PmCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws PlayerFindException {
		if(args.length < 1){
			playerHelper.SendDirectedMessage(commandSender, "Usage: /pm " + GetUsage());
			return;
		}

		Player otherply = playerHelper.MatchPlayerSingle(args[0]);

		String message = Utils.concatArray(args, 1, "");

		playerHelper.SendDirectedMessage(commandSender, "§e[PM >] §f" + otherply.getName() + "§f: " + message);
		playerHelper.SendDirectedMessage(otherply, "§e[PM <] §f" + commandSender.getName() + "§f: " + message);
	}
}
