package de.doridian.yiffbukkit.chat.commands;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.ICommand;
import de.doridian.yiffbukkit.main.commands.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.ICommand.Usage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Names({"conv", "conversation"})
@Help("Opens or closes a conversation with the given player. This means that all your chat is going to them until you close the conversation by running the command without parameters.")
@Usage("[<name>]")
@Permission("yiffbukkit.communication.conversation")
public class ConversationCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		final String playerName = commandSender.getName();
		if (argStr.isEmpty()) {
			String otherName = playerHelper.conversations.get(playerName);
			if (otherName == null)
				throw new YiffBukkitCommandException("No conversation to close.");

			playerHelper.conversations.remove(playerName);

			playerHelper.sendDirectedMessage(commandSender, "Closed conversation with "+otherName+".");
			return;
		}

		final Player otherply = playerHelper.matchPlayerSingle(argStr);
		final String otherName = otherply.getName();
		playerHelper.conversations.put(playerName, otherName);

		playerHelper.sendDirectedMessage(commandSender, "Opened conversation with "+otherName+".");
	}
}
