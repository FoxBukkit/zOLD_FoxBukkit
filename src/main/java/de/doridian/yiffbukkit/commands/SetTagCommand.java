package de.doridian.yiffbukkit.commands;

import org.bukkit.command.CommandSender;
import de.doridian.yiffbukkit.PermissionDeniedException;
import de.doridian.yiffbukkit.util.Utils;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("settag")
//@Help("Sets tag of specified user.")
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

	@Override
	public String GetHelp() {
		StringBuilder sb = new StringBuilder("Sets tag of specified user.\nColors:");
		for (char c = '0'; c <= '9'; ++c) {
			sb.append(" §");
			sb.append(c);
			sb.append('$');
			sb.append(c);
		}
		for (char c = 'a'; c <= 'f'; ++c) {
			sb.append(" §");
			sb.append(c);
			sb.append('$');
			sb.append(c);
		}
		return sb.toString();
	}
}
