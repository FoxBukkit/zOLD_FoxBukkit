package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names({"conv", "conversation"})
@Help("Opens a conversation with the given player.")
@Usage("[<name>]")
@Level(0)
public class ConversationCommand extends ICommand {
	public ConversationCommand(YiffBukkit plug) {
		super(plug);
	}

	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		if (argStr.isEmpty()) {
			playerHelper.conversations.remove(ply.getName());
			return;
		}

		Player otherply = playerHelper.MatchPlayerSingle(argStr);
		String otherName = otherply.getName();
		playerHelper.conversations.put(ply.getName(), otherName);
	}
}
