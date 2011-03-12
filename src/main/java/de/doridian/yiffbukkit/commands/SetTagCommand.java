package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.PermissionDeniedException;
import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.util.Utils;

public class SetTagCommand extends ICommand {
	public int GetMinLevel() {
		return 3;
	}

	public SetTagCommand(YiffBukkit plug) {
		super(plug);
	}

	public void Run(Player ply, String[] args, String argStr) throws PermissionDeniedException {
		String otherName = playerHelper.CompletePlayerName(args[0], false);
		if (otherName == null) {
			return;
		}

		String newTag = Utils.concatArray(args, 1, "").replace('$', '§');
		if (playerHelper.GetPlayerLevel(ply) < playerHelper.GetPlayerLevel(otherName))
			throw new PermissionDeniedException();

		if (newTag.equals("none")) {
			playerHelper.SetPlayerTag(otherName, null);
			playerHelper.SendServerMessage(ply.getName() + " reset tag of " + playerHelper.GetPlayerTag(otherName) + otherName + "§f!");
		}
		else {
			playerHelper.SetPlayerTag(otherName, newTag);
			playerHelper.SendServerMessage(ply.getName() + " set tag of " + newTag + otherName + "§f!");
		}
	}

	public String GetHelp() {
		return "Sets tag of specified user";
	}

	public String GetUsage() {
		return "<name> <tag>|none";
	}
}
