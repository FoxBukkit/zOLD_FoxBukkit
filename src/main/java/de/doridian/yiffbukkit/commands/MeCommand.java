package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.commands.ICommand.*;

@Names({"me", "emote"})
@Help("Well, it's /me, durp")
@Usage("<stuff here>")
@Level(0)
public class MeCommand extends ICommand {
	public void Run(Player ply, String[] args, String argStr) {
		String message = "* "+playerHelper.GetPlayerTag(ply) + ply.getDisplayName() + " " + argStr;

		final String conversationTarget = playerHelper.conversations.get(ply.getName());
		if (conversationTarget == null) {
			plugin.getServer().broadcastMessage(message);
		}
		else {
			message = "§e[CONV]§f "+message;
			ply.sendMessage(message);
			plugin.getServer().getPlayer(conversationTarget).sendMessage(message);
		}
	}
}
