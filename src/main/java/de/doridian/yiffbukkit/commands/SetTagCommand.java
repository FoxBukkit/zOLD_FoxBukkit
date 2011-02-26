package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.Utils;
import de.doridian.yiffbukkit.YiffBukkit;

public class SetTagCommand extends ICommand {
	public int GetMinLevel() {
		return 3;
	}

	public SetTagCommand(YiffBukkit plug) {
		super(plug);
	}

	public void Run(Player ply, String[] args, String argStr) {
		String otherName = playerHelper.CompletePlayerName(args[0], false);
		if (otherName == null) {
			return;
		}

		String newtag = Utils.concatArray(args, 1, "").replace('$', '§');
		if (playerHelper.GetPlayerLevel(ply) < playerHelper.GetPlayerLevel(otherName)) {
			playerHelper.SendPermissionDenied(ply);
			return;
		}
		playerHelper.SetPlayerTag(otherName, newtag);
		playerHelper.SendServerMessage(ply.getName() + " set tag of " + otherName + " to " + newtag + "!");
	}

	public String GetHelp() {
		return "Sets tag of specified user";
	}

	public String GetUsage() {
		return "<name> <tag>|none";
	}
}
