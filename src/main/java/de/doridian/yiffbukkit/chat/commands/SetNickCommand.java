package de.doridian.yiffbukkit.chat.commands;

import de.doridian.yiffbukkit.core.util.MessageHelper;
import de.doridian.yiffbukkit.core.util.PlayerHelper;
import de.doridian.yiffbukkit.main.PermissionDeniedException;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.*;
import de.doridian.yiffbukkit.main.util.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Names("setnick")
@Help(
		"Sets the nick of the specified user.\n" +
		"Colors: \u00a70$0 \u00a71$1 \u00a72$2 \u00a73$3 \u00a74$4 \u00a75$5 \u00a76$6 \u00a77$7 \u00a78$8 \u00a79$9 \u00a7a$a \u00a7b$b \u00a7c$c \u00a7d$d \u00a7e$e \u00a7f$f"
)
@Usage("<name> <nick>|none")
@BooleanFlags("f")
@Permission("yiffbukkit.users.setnick")
public class SetNickCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr, String commandName) throws YiffBukkitCommandException {
		args = parseFlags(args);

		final Player otherPly = playerHelper.matchPlayerSingle(args[0], false);

		final String newNick = Utils.concatArray(args, 1, "").replace('$', '\u00a7');
		if (PlayerHelper.getPlayerLevel(commandSender) < PlayerHelper.getPlayerLevel(otherPly))
			throw new PermissionDeniedException();

		final boolean force = booleanFlags.contains('f');

		final String undoCommand;
		if (otherPly.getName().equals(otherPly.getDisplayName()))
			undoCommand = String.format("/setnick \"%s\" none", otherPly.getName());
		else
			undoCommand = String.format("/setnick \"%s\" %s", otherPly.getName(), otherPly.getDisplayName().replace('\u00a7', '$'));

		if (newNick.equals("none")) {
			otherPly.setDisplayName(otherPly.getName());
			playerHelper.setPlayerNick(otherPly.getUniqueId(), null);
			announceTagChange("%1$s reset nickname of %2$s!", "%2$s reset their own nickname!", commandSender, otherPly, undoCommand);
		}
		else if (!force && newNick.matches("^\u00a7[^\u00a7]*$")) {
			throw new YiffBukkitCommandException("Plainly colored nick detected. This color belongs into the rank tag now (/settag -r).");
		}
		else {
			otherPly.setDisplayName(newNick);
			playerHelper.setPlayerNick(otherPly.getUniqueId(), newNick);
			announceTagChange("%1$s set nickname of %2$s!", "%2$s set their own nickname!", commandSender, otherPly, undoCommand);
		}
	}

	public static void announceTagChange(String formatOther, String formatOwn, CommandSender commandSender, CommandSender modifiedPlayer, String undoCommand) {
		final String format;
		if (commandSender == modifiedPlayer)
			format = formatOwn;
		else
			format = formatOther;

		MessageHelper.sendServerMessage(String.format(
				format + " %3$s",
				MessageHelper.format(commandSender),
				MessageHelper.formatWithTag(modifiedPlayer),
				MessageHelper.button(undoCommand, "undo", "blue", false)
		));
	}
}
