package de.doridian.yiffbukkit.chat.commands;

import de.doridian.yiffbukkit.chat.ChatChannelContainer;
import de.doridian.yiffbukkit.chat.ChatHelper;
import de.doridian.yiffbukkit.chat.ChatReplacer;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.ICommand;
import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;

@ICommand.Names({"crepl"})
@ICommand.Help("Makes chat text replace on certain phrases")
@ICommand.Usage("<from> <to>")
@ICommand.Permission("yiffbukkit.chatreplace")
@ICommand.BooleanFlags("lrd")
public class ChatReplacement extends ICommand {
	public ChatReplacement() {
		super();
	}

	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		args = parseFlags(args);

		ChatChannelContainer cont = ChatHelper.getInstance().container;

		if(cont.replacers == null) {
			cont.replacers = new ArrayList<ChatReplacer>();
		}

		if(booleanFlags.contains('l')) {
			PlayerHelper.sendDirectedMessage(commandSender, "Listing ChatReplacements:");
			for(int i=0;i<cont.replacers.size();i++) {
				PlayerHelper.sendDirectedMessage(commandSender, (i+1)+") "+cont.replacers.get(i));
			}
		} else if(booleanFlags.contains('d')) {
			cont.replacers.remove(Integer.parseInt(args[0]) - 1);
			PlayerHelper.sendDirectedMessage(commandSender, "Removed: " + args[0]);
			ChatHelper.saveChannels();
		} else if(booleanFlags.contains('r')) {
			ChatReplacer.RegexChatReplacer repl = new ChatReplacer.RegexChatReplacer(args[0],  Utils.concatArray(args, 1, null));
			cont.replacers.add(repl);
			PlayerHelper.sendDirectedMessage(commandSender, "Added: " + repl);
			ChatHelper.saveChannels();
		} else {
			ChatReplacer.PlainChatReplacer repl = new ChatReplacer.PlainChatReplacer(args[0],  Utils.concatArray(args, 1, null));
			cont.replacers.add(repl);
			PlayerHelper.sendDirectedMessage(commandSender, "Added: " + repl);
			ChatHelper.saveChannels();
		}
	}
}
