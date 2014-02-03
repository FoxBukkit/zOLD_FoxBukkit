package de.doridian.yiffbukkit.chat.commands;

import de.doridian.yiffbukkit.core.util.MessageHelper;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;
import de.doridian.yiffbukkit.main.util.PlayerFindException;
import de.doridian.yiffbukkit.main.util.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Names({"pm", "msg"})
@Help("Sends a private message to the specified user, that cannot be seen by anyone but the target and yourself.")
@Usage("<name> <text>")
@Permission("yiffbukkit.communication.pm")
public class PmCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		if (args.length < 1) {
			throw new YiffBukkitCommandException("Usage: /pm " + getUsage());
		}

		final Player otherPlayer = playerHelper.matchPlayerSingle(args[0]);
		final String message = Utils.concatArray(args, 1, "");

		MessageHelper.sendMessage(commandSender, String.format("<color name=\"yellow\">[PM &gt;]</color> %1$s: %2$s", MessageHelper.format(otherPlayer), message));
		MessageHelper.sendMessage(otherPlayer, String.format("<color name=\"yellow\">[PM &lt;]</color> %1$s: %2$s", MessageHelper.format(commandSender), message));
	}
}
