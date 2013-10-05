package de.doridian.yiffbukkit.chat.commands;

import de.doridian.yiffbukkit.chat.ChatChannelContainer;
import de.doridian.yiffbukkit.chat.ChatHelper;
import de.doridian.yiffbukkit.chat.ChatReplacer;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.*;
import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import org.bukkit.command.CommandSender;

@Names("crepl")
@Help("Makes chat text replace on certain phrases")
@Usage("<from> <to>")
@Permission("yiffbukkit.chatreplace")
@BooleanFlags("lrd")
public class ChatReplacement extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		args = parseFlags(args);

		ChatChannelContainer cont = ChatHelper.getInstance().container;

		if(booleanFlags.contains('l')) {
			PlayerHelper.sendDirectedMessage(commandSender, "Listing ChatReplacements:");
			for(int i=0;i<cont.replacers.size();i++) {
				final ChatReplacer repl = cont.replacers.get(i);
				PlayerHelper.sendDirectedMessage(commandSender, i + ") " + repl);
			}
		} else if(booleanFlags.contains('d')) {
			final int i = Integer.parseInt(args[0]);
			final ChatReplacer repl = cont.replacers.remove(i);
			PlayerHelper.sendDirectedMessage(commandSender, "Removed: " + i + ") " + repl);
			ChatHelper.saveChannels();
		} else {
			final String to = Utils.concatArray(args, 1, null);
			final ChatReplacer repl;
			if (booleanFlags.contains('r')) {
				repl = new ChatReplacer.RegexChatReplacer(args[0], to);
			} else {
				repl = new ChatReplacer.PlainChatReplacer(args[0], to);
			}
			cont.replacers.add(repl);
			PlayerHelper.sendDirectedMessage(commandSender, "Added: " + repl);
			ChatHelper.saveChannels();
		}
	}
}
