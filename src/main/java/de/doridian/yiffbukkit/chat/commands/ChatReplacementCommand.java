package de.doridian.yiffbukkit.chat.commands;

import de.doridian.yiffbukkit.chat.ChatChannelContainer;
import de.doridian.yiffbukkit.chat.ChatHelper;
import de.doridian.yiffbukkit.chat.ChatReplacer;
import de.doridian.yiffbukkit.chat.ChatReplacer.PlainChatReplacer;
import de.doridian.yiffbukkit.chat.ChatReplacer.RegexChatReplacer;
import de.doridian.yiffbukkit.core.util.MessageHelper;
import de.doridian.yiffbukkit.core.util.PermissionPredicate;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.chat.Parser;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.BooleanFlags;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;
import de.doridian.yiffbukkit.main.util.Utils;
import org.bukkit.command.CommandSender;

@Names("crepl")
@Help("Makes chat text replace on certain phrases")
@Usage("<from> <to>")
@Permission("yiffbukkit.chatreplace")
@BooleanFlags("lrd")
public class ChatReplacementCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		args = parseFlags(args);

		ChatChannelContainer cont = ChatHelper.getInstance().container;

		if (booleanFlags.contains('l')) {
			MessageHelper.sendMessage(commandSender, "Listing ChatReplacements:");
			for (int i = 0; i < cont.replacers.size(); i++) {
				final ChatReplacer repl = cont.replacers.get(i);
				MessageHelper.sendMessage(commandSender, formatReplacement("%2$d) %3$s", commandSender, i, repl, false));
			}

			return;
		}

		if (booleanFlags.contains('d')) {
			final int i = Integer.parseInt(args[0]);
			final ChatReplacer repl = cont.replacers.remove(i);
			MessageHelper.sendServerMessage(new PermissionPredicate("yiffbukkit.chatreplace"), formatReplacement("%1$s removed replacement: %2$d) %3$s", commandSender, i, repl, true));

			ChatHelper.saveChannels();

			return;
		}

		final String to = Utils.concatArray(args, 1, null);
		if (to == null)
			throw new YiffBukkitCommandException("Missing argument.");
		final ChatReplacer repl;
		if (booleanFlags.contains('r')) {
			repl = new RegexChatReplacer(args[0], to);
		}
		else {
			repl = new PlainChatReplacer(args[0], to);
		}
		cont.replacers.add(repl);
		final int i = cont.replacers.size() - 1;
		MessageHelper.sendServerMessage(new PermissionPredicate("yiffbukkit.chatreplace"), formatReplacement("%1$s added replacement: %2$d) %3$s", commandSender, i, repl, false));
		ChatHelper.saveChannels();
	}

	public static String formatReplacement(String format, CommandSender commandSender, int index, ChatReplacer replacer, boolean undoIsAdd) {
		final String button;
		if (undoIsAdd) {
			button = MessageHelper.button(replacer.asCommand(), "restore", "dark_green", false);
		}
		else {
			button = MessageHelper.button("/crepl -d " + index, "x", "red", true);
		}

		return String.format(
				format + " %4$s",
				MessageHelper.format(commandSender),
				index,
				replacer,
				button
		);
	}
}
