package de.doridian.yiffbukkit.commands;

import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.warp.WarpDescriptor;
import de.doridian.yiffbukkit.warp.WarpException;

public class WarpInfoCommand extends ICommand {

	@Override
	public int GetMinLevel() {
		return 0;
	}

	public WarpInfoCommand(YiffBukkit plug) {
		super(plug);
	}

	@Override
	public void Run(Player ply, String[] args, String argStr) {
		if (argStr == "") {
			String playerName = ply.getName();
			StringBuilder sb = new StringBuilder("Available warps: ");
			boolean first = true;
			for (Entry<String, WarpDescriptor> entry : plugin.warpEngine.getWarps().entrySet()) {
				WarpDescriptor warp = entry.getValue();
				int rank = warp.checkAccess(playerName);
				if (rank < 1)
					continue;
				
				if (!first)
					sb.append(", ");

				if (rank == 2) // TODO: use actual rank, not checkAccess
					sb.append("§7@§f");
				else if (rank >= 2)
					sb.append("§7#§f");

				sb.append(warp.name);

				first = false;
			}

			playerHelper.SendDirectedMessage(ply, sb.toString());
			return;
		}
		try {
			WarpDescriptor warp = plugin.warpEngine.getWarp(ply.getName(), args[0]);

			Vector warpLocation = warp.location.toVector();

			long unitsFromYou = Math.round(warpLocation.distance(ply.getLocation().toVector()));
			long unitsFromSpawn = Math.round(warpLocation.distance(ply.getWorld().getSpawnLocation().toVector()));

			playerHelper.SendDirectedMessage(ply, "Warp §9" + warp.name + "§f is owned by "+warp.getOwner());
			if (warp.isPublic)
				playerHelper.SendDirectedMessage(ply, "Warp is public");
			else
				playerHelper.SendDirectedMessage(ply, "Warp is private");
			
			StringBuilder sb = new StringBuilder("Access list: ");
			boolean first = true;
			for (Entry<String, Integer> entry : warp.getRanks().entrySet()) {
				if (!first)
					sb.append(", ");

				if (entry.getValue() >= 2)
					sb.append('@');

				sb.append(entry.getKey());

				first = false;
			}
			playerHelper.SendDirectedMessage(ply, sb.toString());
			
			playerHelper.SendDirectedMessage(
					ply, "This warp is " +
					unitsFromYou + "m from you and " +
					unitsFromSpawn + "m from the spawn.");
		}
		catch (WarpException e) {
			playerHelper.SendDirectedMessage(ply, e.getMessage());
		}
	}

	@Override
	public String GetHelp() {
		return "Gets information about the specified warp name or lists all warps available to you. §7@§f means op, §7#§f means owner.";
	}

	@Override
	public String GetUsage() {
		return "[<warp point name>]";
	}
}
