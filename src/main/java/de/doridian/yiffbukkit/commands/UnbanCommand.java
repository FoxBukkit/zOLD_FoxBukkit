package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkit;

public class UnbanCommand extends ICommand {
	public int GetMinLevel() {
		return 3;
	}

	public UnbanCommand(YiffBukkit plug) {
		super(plug);
	}

	public void Run(Player ply, String[] args, String argStr) {
		String otherply = args[0];
		if(!plugin.playerHelper.GetPlayerRank(otherply).equals("banned")) {
			plugin.playerHelper.SendDirectedMessage(ply, "Player is not banned!");
			return;
		}

		plugin.playerHelper.SetPlayerRank(otherply, "guest");
		plugin.playerHelper.SendServerMessage(ply.getName() + " unbanned " + otherply + "!");
	}

	public String GetHelp() {
		return "Unbans specified user";
	}

	public String GetUsage() {
		return "<full name>";
	}
}
