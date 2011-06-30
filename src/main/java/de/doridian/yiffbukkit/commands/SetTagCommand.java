package de.doridian.yiffbukkit.commands;

import org.bukkit.command.CommandSender;
import de.doridian.yiffbukkit.PermissionDeniedException;
import de.doridian.yiffbukkit.util.Utils;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("settag")
@Help(
		"Sets the tag of the specified user.\n" +
		"Colors: §0$0 §1$1 §2$2 §3$3 §4$4 §5$5 §6$6 §7$7 §8$8 §9$9 §a$a §b$b §c$c §d$d §e$e §f$f"
)
@Usage("<name> <tag>|none")
@Level(3)
public class SetTagCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws PermissionDeniedException {
		String otherName = playerHelper.completePlayerName(args[0], false);
		if (otherName == null) {
			return;
		}

		String newTag = Utils.concatArray(args, 1, "").replace('$', '§');
		if (playerHelper.getPlayerLevel(commandSender) < playerHelper.getPlayerLevel(otherName))
			throw new PermissionDeniedException();

		if (newTag.equals("none")) {
			playerHelper.setPlayerTag(otherName, null);
			playerHelper.sendServerMessage(commandSender.getName() + " reset tag of " + playerHelper.getPlayerTag(otherName) + otherName + "§f!");
		}
		else {
			playerHelper.setPlayerTag(otherName, newTag);
			playerHelper.sendServerMessage(commandSender.getName() + " set tag of " + newTag + otherName + "§f!");
		}
	}
}
