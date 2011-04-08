package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.commands.ICommand.Help;
import de.doridian.yiffbukkit.commands.ICommand.Level;
import de.doridian.yiffbukkit.commands.ICommand.Names;
import de.doridian.yiffbukkit.commands.ICommand.Usage;

@Names({"me", "emote"})
@Help("Well, its /me, durp")
@Usage("<stuff here>")
@Level(0)
public class MeCommand extends ICommand {
	public MeCommand(YiffBukkit plug) {
		super(plug);
	}

	public void Run(Player ply, String[] args, String argStr) {
		String conversationTarget = playerHelper.conversations.get(ply.getName());
		String message = "* "+playerHelper.GetPlayerTag(ply) + ply.getName() + " " + argStr;
		if (conversationTarget == null) {
			plugin.getServer().broadcastMessage(message);
		}
		else {
			message = "§e[CONV]§f"+message;
			ply.sendMessage(message);
			plugin.getServer().getPlayer(conversationTarget).sendMessage(message);
		}
	}
}
