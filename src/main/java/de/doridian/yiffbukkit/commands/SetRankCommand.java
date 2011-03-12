package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.PermissionDeniedException;
import de.doridian.yiffbukkit.YiffBukkit;

public class SetRankCommand extends ICommand {
	public int GetMinLevel() {
		return 3;
	}

	public SetRankCommand(YiffBukkit plug) {
		super(plug);
	}

	public void Run(Player ply, String[] args, String argStr) throws PermissionDeniedException {
		String otherply = args[0];
		String newrank = args[1];
		int selflvl = playerHelper.GetPlayerLevel(ply);

		if(!playerHelper.ranklevels.containsKey(newrank)) {
			playerHelper.SendDirectedMessage(ply, "Rank does not exist!");
			return;
		}

		if(selflvl <= playerHelper.GetPlayerLevel(otherply) || selflvl <= playerHelper.GetRankLevel(newrank))
			throw new PermissionDeniedException();

		playerHelper.SetPlayerRank(otherply, newrank);
		playerHelper.SendServerMessage(ply.getName() + " set rank of " + otherply + " to " + newrank);
	}

	public String GetHelp() {
		return "Sets rank of specified user";
	}

	public String GetUsage() {
		return "<full name> <rank>";
	}
}
