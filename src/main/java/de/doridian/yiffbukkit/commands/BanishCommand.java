package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import de.doridian.yiffbukkit.YiffBukkit;

public class BanishCommand extends ICommand {
	public int GetMinLevel() {
		return 3;
	}

	public BanishCommand(YiffBukkit plug) {
		super(plug);
	}

	public void Run(Player ply, String[] args, String argStr) {
		boolean resetHome = args.length >= 2 && (args[1].equals("resethome") || args[1].equals("sethome") || args[1].equals("withhome"));

		Player otherply = plugin.playerHelper.MatchPlayerSingle(ply, args[0]);
		if (otherply == null) return;

		int level = plugin.playerHelper.GetPlayerLevel(ply);
		int otherlevel = plugin.playerHelper.GetPlayerLevel(otherply);

		// Players with the same levels can banish each other, but not reset each other's homes
		if (level < otherlevel || (level == otherlevel && resetHome)) {
			plugin.playerHelper.SendPermissionDenied(ply);
			return;
		}

		Vector previousPos = otherply.getLocation().toVector();
		otherply.teleportTo(otherply.getWorld().getSpawnLocation());

		if (resetHome) {
			plugin.playerHelper.SetPlayerHomePosition(otherply, otherply.getWorld().getSpawnLocation());
		}
		else {
			Vector homePos = plugin.playerHelper.GetPlayerHomePosition(otherply).toVector();

			long unitsFromPrevious = Math.round(homePos.distance(previousPos));
			long unitsFromYou = Math.round(homePos.distance(ply.getLocation().toVector()));
			long unitsFromSpawn = Math.round(homePos.distance(otherply.getWorld().getSpawnLocation().toVector()));

			plugin.playerHelper.SendDirectedMessage(
					ply, otherply.getName() + "'s home is " +
					unitsFromPrevious + "m from the previous location, " +
					unitsFromYou + "m from you and " +
					unitsFromSpawn + "m from the spawn. Use '!banish " + otherply.getName() + " resethome' to move it to the spawn.");
		}

		plugin.playerHelper.SendServerMessage(ply.getName() + " banished " + otherply.getName() + (resetHome ? " and reset his/her home position!" : "!"));
	}

	public String GetHelp() {
		return "Banishes the specified user to the spawn and optionally resets their home location.";
	}

	public String GetUsage() {
		return "<name> [resethome]";
	}
}
