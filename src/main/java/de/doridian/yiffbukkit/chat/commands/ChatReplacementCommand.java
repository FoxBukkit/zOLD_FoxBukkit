/**
 * This file is part of YiffBukkit.
 *
 * YiffBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * YiffBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with YiffBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.doridian.yiffbukkit.chat.commands;

import de.doridian.yiffbukkit.chat.ChatReplacer;
import de.doridian.yiffbukkit.chat.ChatReplacer.PlainChatReplacer;
import de.doridian.yiffbukkit.chat.ChatReplacer.RegexChatReplacer;
import de.doridian.yiffbukkit.core.YiffBukkit;
import de.doridian.yiffbukkit.core.util.MessageHelper;
import de.doridian.yiffbukkit.core.util.PermissionPredicate;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.BooleanFlags;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;
import de.doridian.yiffbukkit.main.util.Utils;
import org.bukkit.command.CommandSender;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

@Names("crepl")
@Help("Makes chat text replace on certain phrases")
@Usage("<from> <to>")
@Permission("yiffbukkit.chatreplace")
@BooleanFlags("lrd")
public class ChatReplacementCommand extends ICommand {
	public static List<ChatReplacer> chatReplacers = new ArrayList<>();

	public ChatReplacementCommand() {
		loadReplacers();
	}

	@Override
	public void run(CommandSender commandSender, String[] args, String argStr, String commandName) throws YiffBukkitCommandException {
		args = parseFlags(args);

		if (booleanFlags.contains('l')) {
			MessageHelper.sendMessage(commandSender, "Listing ChatReplacements:");
			for (int i = 0; i < chatReplacers.size(); i++) {
				final ChatReplacer repl = chatReplacers.get(i);
				MessageHelper.sendMessage(commandSender, formatReplacement("%2$d) %3$s", commandSender, i, repl, false));
			}
			return;
		}

		if (booleanFlags.contains('d')) {
			final int i = Integer.parseInt(args[0]);
			final ChatReplacer repl = chatReplacers.remove(i);
			MessageHelper.sendServerMessage(new PermissionPredicate("yiffbukkit.chatreplace"), formatReplacement("%1$s removed replacement: %2$d) %3$s", commandSender, i, repl, true));
			saveReplacers();
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
		chatReplacers.add(repl);
		final int i = chatReplacers.size() - 1;
		MessageHelper.sendServerMessage(new PermissionPredicate("yiffbukkit.chatreplace"), formatReplacement("%1$s added replacement: %2$d) %3$s", commandSender, i, repl, false));
		saveReplacers();
	}

	public static void loadReplacers() {
		try {
			FileInputStream stream = new FileInputStream(YiffBukkit.instance.getDataFolder() + "/chat_replacers.dat");
			ObjectInputStream reader = new ObjectInputStream(stream);
			chatReplacers = (List<ChatReplacer>) reader.readObject();
			reader.close();
			stream.close();
		} catch (FileNotFoundException e) {
			chatReplacers = new ArrayList<>();
		} catch (Exception e) {
			chatReplacers = new ArrayList<>();
			e.printStackTrace();
		}
	}

	public static void saveReplacers() {
		try {
			FileOutputStream stream = new FileOutputStream(YiffBukkit.instance.getDataFolder() + "/chat_replacers.dat");
			ObjectOutputStream writer = new ObjectOutputStream(stream);
			writer.writeObject(chatReplacers);
			writer.close();
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
