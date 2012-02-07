package de.doridian.yiffbukkit.chat.commands;

import de.doridian.yiffbukkit.main.commands.ICommand;
import de.doridian.yiffbukkitsplit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.chat.ChatChannel;
import de.doridian.yiffbukkit.chat.ChatHelper;
import de.doridian.yiffbukkit.main.commands.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.ICommand.Usage;
import org.bukkit.entity.Player;

@Names({"me", "emote"})
@Help("Well, it's /me, durp")
@Usage("<stuff here>")
@Permission("yiffbukkitsplit.communication.emote")
public class MeCommand extends ICommand {
	@Override
	public void Run(Player commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		String message = "\u00a77* "+playerHelper.getPlayerTag(commandSender) + commandSender.getDisplayName() + "\u00a77 " + argStr;

		final String conversationTarget = playerHelper.conversations.get(commandSender.getName());
		if (conversationTarget == null) {
			final ChatHelper helper = ChatHelper.getInstance();
			final ChatChannel chan = helper.getActiveChannel(commandSender);
			final String msg = "* " + commandSender.getName() + " " + argStr;
			if(chan == helper.OOC) {
				plugin.ircbot.sendToPublicChannel(msg);
				plugin.sendConsoleMsg(msg, false);
			} else {
				plugin.sendConsoleMsg("[" + chan.name + "] " + msg, false);
			}
			helper.sendChat(asPlayer(commandSender), message, false);
		}
		else {
			message = "\u00a7e[CONV]\u00a7f "+message;
			commandSender.sendMessage(message);
			plugin.getServer().getPlayer(conversationTarget).sendMessage(message);
		}
	}
}
