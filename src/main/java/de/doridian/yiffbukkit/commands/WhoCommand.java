package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkit;

public class WhoCommand extends ICommand {

	public int GetMinLevel() {
		return 0;
	}

	public WhoCommand(YiffBukkit plug) {
		super(plug);
	}

	public void Run(Player ply, String[] args, String argStr) {
		if(args.length > 0) {
			Player target = playerHelper.MatchPlayerSingle(ply, args[0]);
			if(target == null) return;
			playerHelper.SendDirectedMessage(ply, "Name: " + target.getName());
			playerHelper.SendDirectedMessage(ply, "Rank: " + playerHelper.GetPlayerRank(target));
			playerHelper.SendDirectedMessage(ply, "NameTag: " + playerHelper.GetFullPlayerName(target));
			playerHelper.SendDirectedMessage(ply, "World: " + target.getWorld().getName());
			if(playerHelper.GetPlayerLevel(ply) < 3) return;
			playerHelper.SendDirectedMessage(ply, "IP: " + target.getAddress().getAddress().toString().substring(1));
		} else {
			Player[] players = plugin.getServer().getOnlinePlayers();
			String str = "Online players: " + players[0].getName();
			for(int i=1;i<players.length;i++) {
				str += ", " + players[i].getName();
			}
			playerHelper.SendDirectedMessage(ply, str);
		}
	}

	public String GetHelp() {
		return "Prints user list if used without parameters or information about the specified user";
	}

	public String GetUsage() {
		return "[name]";
	}
}
