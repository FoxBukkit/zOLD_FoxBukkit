package de.doridian.yiffbukkit.chat.commands;

import de.doridian.yiffbukkit.chat.ChatChannel;
import de.doridian.yiffbukkit.chat.ChatHelper;
import de.doridian.yiffbukkit.main.commands.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.ICommand.Usage;

@Names({"ooc", "o"})
@Help("Uses the OOC channel")
@Usage("[message]")
@Permission("yiffbukkitsplit.channels.say.ooc")
public class OOCCommand extends GenericChannelCommand {
	@Override
	protected ChatChannel getChannel() {
		return ChatHelper.getInstance().OOC;
	}
}
