package de.doridian.yiffbukkit.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.PermissionDeniedException;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.util.Utils;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("setnick")
@Help(
		"Sets the nick of the specified user.\n" +
		"Colors: §0$0 §1$1 §2$2 §3$3 §4$4 §5$5 §6$6 §7$7 §8$8 §9$9 §a$a §b$b §c$c §d$d §e$e §f$f"
)
@Usage("<name> <nick>|none")
@Level(5)
@Permission("yiffbukkit.users.setnick")
public class SetNickCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		String otherName = playerHelper.completePlayerName(args[0], false);

		if (otherName == null)
			throw new YiffBukkitCommandException("No unique player found.");

		Player otherPly = playerHelper.matchPlayerSingle(args[0]);

		String newNick = Utils.concatArray(args, 1, "").replace('$', '§');
		if (playerHelper.getPlayerLevel(commandSender) < playerHelper.getPlayerLevel(otherName))
			throw new PermissionDeniedException();

		if (newNick.equals("none")) {
			otherPly.setDisplayName(otherName);
			playerHelper.setPlayerNick(otherName, null);
			playerHelper.sendServerMessage(commandSender.getName() + " reset nickname of " + otherName + "§f!");
		}
		else {
			otherPly.setDisplayName(newNick);
			playerHelper.setPlayerNick(otherName, newNick);
			playerHelper.sendServerMessage(commandSender.getName() + " set nickname of " + otherName + " to " + newNick + "§f!");
		}
	}
}
