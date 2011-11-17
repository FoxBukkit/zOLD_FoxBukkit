package de.doridian.yiffbukkit.commands;

import org.bukkit.command.CommandSender;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.chat.ChatHelper;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names({"me", "emote"})
@Help("Well, it's /me, durp")
@Usage("<stuff here>")
@Permission("yiffbukkit.communication.emote")
public class MeCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		String message = "\u00a77* "+playerHelper.getPlayerTag(commandSender) + commandSender.getDisplayName() + "\u00a77 " + argStr;

		final String conversationTarget = playerHelper.conversations.get(commandSender.getName());
		if (conversationTarget == null) {
			if(ChatHelper.getInstance().getActiveChannel(asPlayer(commandSender)) == ChatHelper.getInstance().OOC) {
				plugin.ircbot.sendToPublicChannel("* " + commandSender.getName() + " " + argStr);
			}
			ChatHelper.getInstance().sendChat(asPlayer(commandSender), message, false);
		}
		else {
			message = "\u00a7e[CONV]\u00a7f "+message;
			commandSender.sendMessage(message);
			plugin.getServer().getPlayer(conversationTarget).sendMessage(message);
		}
	}
}
