package de.doridian.yiffbukkit.commands;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.chat.ChatChannel;
import de.doridian.yiffbukkit.chat.ChatHelper;
import org.bukkit.entity.Player;

public abstract class GenericChannelCommand extends ICommand {
	protected ChatChannel MYCHANNEL;
	
	protected ChatChannel getChannelInt() {
		if(MYCHANNEL == null) {
			MYCHANNEL = getChannel();
		}
		
		return MYCHANNEL;
	}
	
	protected abstract ChatChannel getChannel();
	
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		ChatHelper.getInstance().sendChat(ply, argStr, true, getChannelInt());
	}
}
