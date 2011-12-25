package de.doridian.yiffbukkit.commands;

import de.doridian.yiffbukkit.PermissionDeniedException;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.Help;
import de.doridian.yiffbukkit.commands.ICommand.Names;
import de.doridian.yiffbukkit.commands.ICommand.Permission;
import de.doridian.yiffbukkit.commands.ICommand.Usage;
import de.doridian.yiffbukkit.util.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Names("setnick")
@Help(
		"Sets the nick of the specified user.\n" +
		"Colors: \u00a70$0 \u00a71$1 \u00a72$2 \u00a73$3 \u00a74$4 \u00a75$5 \u00a76$6 \u00a77$7 \u00a78$8 \u00a79$9 \u00a7a$a \u00a7b$b \u00a7c$c \u00a7d$d \u00a7e$e \u00a7f$f"
)
@Usage("<name> <nick>|none")
@Permission("yiffbukkit.users.setnick")
public class SetNickCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		String otherName = playerHelper.completePlayerName(args[0], false);

		if (otherName == null)
			throw new YiffBukkitCommandException("No unique player found.");

		Player otherPly = playerHelper.matchPlayerSingle(args[0]);

		String newNick = Utils.concatArray(args, 1, "").replace('$', '\u00a7');
		if (playerHelper.getPlayerLevel(commandSender) < playerHelper.getPlayerLevel(otherName))
			throw new PermissionDeniedException();

		if (newNick.equals("none")) {
			otherPly.setDisplayName(otherName);
			playerHelper.setPlayerNick(otherName, null);
			playerHelper.sendServerMessage(commandSender.getName() + " reset nickname of " + otherName + "\u00a7f!");
		}
		else {
			otherPly.setDisplayName(newNick);
			playerHelper.setPlayerNick(otherName, newNick);
			playerHelper.sendServerMessage(commandSender.getName() + " set nickname of " + otherName + " to " + newNick + "\u00a7f!");
		}
	}
}
