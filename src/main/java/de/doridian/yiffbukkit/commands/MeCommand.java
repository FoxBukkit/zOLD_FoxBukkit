package de.doridian.yiffbukkit.commands;

import org.bukkit.command.CommandSender;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names({"me", "emote"})
@Help("Well, it's /me, durp")
@Usage("<stuff here>")
@Level(0)
@Permission("yiffbukkit.communication.emote")
public class MeCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) {
		String message = "§7* "+playerHelper.getPlayerTag(commandSender) + commandSender.getDisplayName() + "§7 " + argStr;

		final String conversationTarget = playerHelper.conversations.get(commandSender.getName());
		if (conversationTarget == null) {
			plugin.getServer().broadcastMessage(message);
		}
		else {
			message = "§e[CONV]§f "+message;
			commandSender.sendMessage(message);
			plugin.getServer().getPlayer(conversationTarget).sendMessage(message);
		}
	}
}
