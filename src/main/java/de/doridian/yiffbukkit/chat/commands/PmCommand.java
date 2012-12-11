package de.doridian.yiffbukkit.chat.commands;

import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;
import de.doridian.yiffbukkit.main.util.PlayerFindException;
import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Names({"pm", "msg"})
@Help("Sends a private message to the specified user, that cannot be seen by anyone but the target and yourself.")
@Usage("<name> <text>")
@Permission("yiffbukkit.communication.pm")
public class PmCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws PlayerFindException {
		if(args.length < 1){
			PlayerHelper.sendDirectedMessage(commandSender, "Usage: /pm " + getUsage());
			return;
		}

		Player otherply = playerHelper.matchPlayerSingle(args[0]);

		String message = Utils.concatArray(args, 1, "");

		PlayerHelper.sendDirectedMessage(commandSender, "\u00a7e[PM >] \u00a7f" + otherply.getName() + "\u00a7f: " + message);
		PlayerHelper.sendDirectedMessage(otherply, "\u00a7e[PM <] \u00a7f" + commandSender.getName() + "\u00a7f: " + message);
	}
}
