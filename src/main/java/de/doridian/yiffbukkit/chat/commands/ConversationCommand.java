package de.doridian.yiffbukkit.chat.commands;

import de.doridian.yiffbukkit.core.util.PlayerHelper;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@Names({"conv", "conversation"})
@Help("Opens or closes a conversation with the given player. This means that all your chat is going to them until you close the conversation by running the command without parameters.")
@Usage("[<name>]")
@Permission("yiffbukkit.communication.conversation")
public class ConversationCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		final UUID playerName = commandSender.getUniqueId();
		if (argStr.isEmpty()) {
			UUID otherName = playerHelper.conversations.get(playerName);
			if (otherName == null)
				throw new YiffBukkitCommandException("No conversation to close.");

			playerHelper.conversations.remove(playerName);

			PlayerHelper.sendDirectedMessage(commandSender, "Closed conversation with "+otherName+".");
			return;
		}

		final Player otherply = playerHelper.matchPlayerSingle(argStr);
		final UUID otherName = otherply.getUniqueId();
		playerHelper.conversations.put(playerName, otherName);

		PlayerHelper.sendDirectedMessage(commandSender, "Opened conversation with "+otherName+".");
	}
}
