package de.doridian.yiffbukkit.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.PermissionDeniedException;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.util.Utils;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("setnick")
//@Help("Sets nick of specified user.")
@Usage("<name> <nick>|none")
@Level(6) //Dori doesnt want people changing nicks :O
public class SetNickCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		String otherName = playerHelper.CompletePlayerName(args[0], false);

		if (otherName == null) {
			return;
		}

		Player otherPly = playerHelper.MatchPlayerSingle(args[0]);

		String newNick = Utils.concatArray(args, 1, "").replace('$', '§');
		if (playerHelper.GetPlayerLevel(commandSender) < playerHelper.GetPlayerLevel(otherName))
			throw new PermissionDeniedException();

		if (newNick.equals("none")) {
			otherPly.setDisplayName(otherName);
			playerHelper.SetPlayerNick(otherName, null);
			playerHelper.SendServerMessage(commandSender.getName() + " reset nickname of " + otherName + "§f!");
		}
		else {
			otherPly.setDisplayName(newNick);
			playerHelper.SetPlayerNick(otherName, newNick);
			playerHelper.SendServerMessage(commandSender.getName() + " set nickname of " + otherName + " to " + newNick + "§f!");
		}
	}

	@Override
	public String GetHelp() {
		StringBuilder sb = new StringBuilder("Sets nick of specified user.\nColors:");
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
