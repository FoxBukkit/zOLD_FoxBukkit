package de.doridian.yiffbukkit.commands;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.chat.ChatChannel;
import de.doridian.yiffbukkit.chat.ChatHelper;
import org.bukkit.entity.Player;

public abstract class GenericChannelCommand extends ICommand {
	private ChatChannel myChannel;

	private ChatChannel getChannelInternal() {
		if (myChannel == null) {
			myChannel = getChannel();
		}

		return myChannel;
	}

	protected abstract ChatChannel getChannel();

	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		ChatHelper.getInstance().sendChat(ply, argStr, true, getChannelInternal());
	}
}
