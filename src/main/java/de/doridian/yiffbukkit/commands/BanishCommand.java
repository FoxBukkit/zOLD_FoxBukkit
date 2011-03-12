package de.doridian.yiffbukkit.commands;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import de.doridian.yiffbukkit.PermissionDeniedException;
import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.util.PlayerFindException;

public class BanishCommand extends ICommand {
	public int GetMinLevel() {
		return 3;
	}

	public BanishCommand(YiffBukkit plug) {
		super(plug);
	}

	public void Run(Player ply, String[] args, String argStr) throws PlayerFindException, PermissionDeniedException {
		boolean resetHome = args.length >= 2 && (args[1].equals("resethome") || args[1].equals("sethome") || args[1].equals("withhome"));

		Player otherply = playerHelper.MatchPlayerSingle(args[0]);

		int level = playerHelper.GetPlayerLevel(ply);
		int otherlevel = playerHelper.GetPlayerLevel(otherply);

		// Players with the same levels can banish each other, but not reset each other's homes
		if (level < otherlevel || (level == otherlevel && resetHome))
			throw new PermissionDeniedException();

		Vector previousPos = otherply.getLocation().toVector();
		Location teleportTarget = otherply.getWorld().getSpawnLocation();
		otherply.teleportTo(teleportTarget);

		if (resetHome) {
			playerHelper.SetPlayerHomePosition(otherply, teleportTarget);
		}
		else {
			Vector homePos = playerHelper.GetPlayerHomePosition(otherply).toVector();

			long unitsFromPrevious = Math.round(homePos.distance(previousPos));
			long unitsFromYou = Math.round(homePos.distance(ply.getLocation().toVector()));
			long unitsFromSpawn = Math.round(homePos.distance(otherply.getWorld().getSpawnLocation().toVector()));

			playerHelper.SendDirectedMessage(
					ply, otherply.getName() + "'s home is " +
					unitsFromPrevious + "m from the previous location, " +
					unitsFromYou + "m from you and " +
					unitsFromSpawn + "m from the spawn. Use '!banish " + otherply.getName() + " resethome' to move it to the spawn.");
		}

		playerHelper.SendServerMessage(ply.getName() + " banished " + otherply.getName() + (resetHome ? " and reset his/her home position!" : "!"));
	}

	public String GetHelp() {
		return "Banishes the specified user to the spawn and optionally resets their home location.";
	}

	public String GetUsage() {
		return "<name> [resethome]";
	}
}
