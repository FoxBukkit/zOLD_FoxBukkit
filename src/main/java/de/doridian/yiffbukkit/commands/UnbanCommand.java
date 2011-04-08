package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkitPlayerListener;

public class UnbanCommand extends ICommand {
	public int GetMinLevel() {
		return 3;
	}

	public UnbanCommand(YiffBukkitPlayerListener playerListener) {
		super(playerListener);
	}

	public void Run(Player ply, String[] args, String argStr) {
		String otherply = args[0];
		if(!playerHelper.GetPlayerRank(otherply).equals("banned")) {
			playerHelper.SendDirectedMessage(ply, "Player is not banned!");
			return;
		}

		playerHelper.SetPlayerRank(otherply, "guest");
		playerHelper.SendServerMessage(ply.getName() + " unbanned " + otherply + "!");
	}

	public String GetHelp() {
		return "Unbans specified user";
	}

	public String GetUsage() {
		return "<full name>";
	}
}
