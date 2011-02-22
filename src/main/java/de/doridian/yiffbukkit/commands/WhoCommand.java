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
			Player target = plugin.playerHelper.MatchPlayerSingle(ply, args[0]);
			if(target == null) return;
			plugin.playerHelper.SendDirectedMessage(ply, "Name: " + target.getName());
			plugin.playerHelper.SendDirectedMessage(ply, "Rank: " + plugin.playerHelper.GetPlayerRank(target));
			plugin.playerHelper.SendDirectedMessage(ply, "NameTag: " + plugin.playerHelper.GetFullPlayerName(target));
			plugin.playerHelper.SendDirectedMessage(ply, "World: " + target.getWorld().getName());
			if(plugin.playerHelper.GetPlayerLevel(ply) < 3) return;
			plugin.playerHelper.SendDirectedMessage(ply, "IP: " + target.getAddress().getAddress().toString().substring(1));
		} else {
			Player[] players = plugin.getServer().getOnlinePlayers();
			String str = "Online players: " + players[0].getName();
			for(int i=1;i<players.length;i++) {
				str += ", " + players[i].getName();
			}
			plugin.playerHelper.SendDirectedMessage(ply, str);
		}
	}

	public String GetHelp() {
		return "Prints user list if used without parameters or information about the specified user";
	}

	public String GetUsage() {
		return "[name]";
	}
}
