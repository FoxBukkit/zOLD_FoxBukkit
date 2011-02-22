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
		Player otherply = plugin.playerHelper.MatchPlayerSingle(ply, args[0]);
		if(otherply == null) return;

		String newtag = Utils.concatArray(args, 1, "").replace('$', '§');
		if(plugin.playerHelper.GetPlayerLevel(ply) < plugin.playerHelper.GetPlayerLevel(otherply)) {
			plugin.playerHelper.SendPermissionDenied(ply);
			return;
		}
		plugin.playerHelper.SetPlayerTag(otherply.getName(), newtag);
		plugin.playerHelper.SendServerMessage(ply.getName() + " set tag of " + otherply.getName() + " to " + newtag + "!");
	}

	public String GetHelp() {
		return "Sets tag of specified user";
	}

	public String GetUsage() {
		return "<name> <tag or none>";
	}
}
