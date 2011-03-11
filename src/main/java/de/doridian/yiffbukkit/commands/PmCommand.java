package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.util.PlayerFindException;
import de.doridian.yiffbukkit.util.Utils;

public class PmCommand extends ICommand {

	public int GetMinLevel() {
		return 0;
	}

	public PmCommand(YiffBukkit plug) {
		super(plug);
	}

	public void Run(Player ply, String[] args, String argStr) throws PlayerFindException {
		if(args.length < 1){
			playerHelper.SendDirectedMessage(ply, "Usage: /pm " + GetUsage());
			return;
		}

		Player otherply = playerHelper.MatchPlayerSingle(args[0]);

		String message = Utils.concatArray(args, 1, "");

		playerHelper.SendDirectedMessage(ply, "§e[PM >] §f" + otherply.getName() + "§f: " + message);
		playerHelper.SendDirectedMessage(otherply, "§e[PM <] §f" + ply.getName() + "§f: " + message);
	}

	public String GetHelp() {
		return "Sends a private message to the specified user, that cannot be seen by anyone but the target and yourself.";
	}

	public String GetUsage() {
		return "<name> <text>";
	}
}
