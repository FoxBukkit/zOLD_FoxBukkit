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
		if (conversationTarget == null)
			plugin.getServer().broadcastMessage("* "+playerHelper.GetPlayerTag(ply) + ply.getName() + " " + argStr);
		else
			plugin.getServer().getPlayer(conversationTarget).sendMessage("* "+playerHelper.GetPlayerTag(ply) + ply.getName() + " " + argStr);
	}
}
