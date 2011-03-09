package de.doridian.yiffbukkit.commands;

import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.warp.WarpDescriptor;
import de.doridian.yiffbukkit.warp.WarpException;

public class WarpCommand extends ICommand {
	@Override
	public int GetMinLevel() {
		return 0;
	}

	public WarpCommand(YiffBukkit plug) {
		super(plug);
	}

	@Override
	public void Run(Player ply, String[] args, String argStr) throws WarpException {
		if (plugin.jailEngine.isJailed(ply)) {
			playerHelper.SendDirectedMessage(ply, "You are jailed!");
			return;
		}

		String playerName = ply.getName();
		if (argStr.isEmpty()) {
			//warp
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
		if (argStr.equals("help")) {
			//warp help
			playerHelper.SendDirectedMessage(ply, "/warp <warp point name> [<command>[ <args>]]");
			playerHelper.SendDirectedMessage(ply, "commands:");
			playerHelper.SendDirectedMessage(ply, "without arguments - teleport to warp");
			playerHelper.SendDirectedMessage(ply, "info - Shows information");
			playerHelper.SendDirectedMessage(ply, "changeowner <new owner> - Transfers ownership");
			playerHelper.SendDirectedMessage(ply, "public|private - Change public access");
			playerHelper.SendDirectedMessage(ply, "addguest <name> - Grant guest access (can teleport)");
			playerHelper.SendDirectedMessage(ply, "addop <name> - Grant op access (can add guests)");
			playerHelper.SendDirectedMessage(ply, "deny <name> - Deny access");
			playerHelper.SendDirectedMessage(ply, "move - Move the warp to your current position");
			playerHelper.SendDirectedMessage(ply, "remove - Deletes the warp. This cannot be undone!");
			return;
		}

		try {
			if (args.length == 1) {
				//warp <warp point name>
				ply.teleportTo(plugin.warpEngine.getWarp(playerName, args[0]).location);
				return;
			}

			String command = args[1].toLowerCase();

			WarpDescriptor warp = plugin.warpEngine.getWarp(playerName, args[0]);
			int rank = warp.checkAccess(playerName);

			if (command.equals("chown") || command.equals("changeowner")) {
				//warp <warp point name> changeowner <new owner> 
				String newOwnerName = playerHelper.CompletePlayerName(args[2], false);
				warp.setOwner(playerName, newOwnerName);

				playerHelper.SendDirectedMessage(ply, "Transferred ownership of warp §9" + warp.name + "§f to "+newOwnerName+".");
			}
			else if (command.equals("public") || command.equals("unlock")) {
				//warp <warp point name> public 
				if (rank < 2)
					throw new WarpException("Permission denied");

				warp.isPublic = true;

				playerHelper.SendDirectedMessage(ply, "Set warp §9" + warp.name + "§f to public.");
			}
			else if (command.equals("private") || command.equals("lock")) {
				//warp <warp point name> private 
				warp.isPublic = false;

				playerHelper.SendDirectedMessage(ply, "Set warp §9" + warp.name + "§f to private.");
			}
			else if (command.equals("deny")) {
				//warp <warp point name> deny <name> 
				String targetName = playerHelper.CompletePlayerName(args[2], false);
				if (targetName == null)
					throw new WarpException("Player not found.");

				warp.setAccess(playerName, targetName, 0);

				playerHelper.SendDirectedMessage(ply, "Revoked " + targetName + "'s access to warp §9" + warp.name + "§f.");
			}
			else if (command.equals("addguest")) {
				//warp <warp point name> addguest <name> 
				String targetName = playerHelper.CompletePlayerName(args[2], false);
				if (targetName == null)
					throw new WarpException("Player not found.");

				warp.setAccess(playerName, targetName, 1);

				playerHelper.SendDirectedMessage(ply, "Granted " + targetName + " guest access to warp §9" + warp.name + "§f.");
			}
			else if (command.equals("addop")) {
				//warp <warp point name> addop <name> 
				String targetName = playerHelper.CompletePlayerName(args[2], false);
				if (targetName == null)
					throw new WarpException("Player not found.");

				warp.setAccess(playerName, targetName, 2);

				playerHelper.SendDirectedMessage(ply, "Granted " + targetName + " op access to warp §9" + warp.name + "§f.");
			}
			else if (command.equals("move")) {
				//warp <warp point name> move 
				if (rank < 3)
					throw new WarpException("You need to be the warp's owner to do this.");

				warp.location = ply.getLocation().clone();

				playerHelper.SendDirectedMessage(ply, "Moved warp §9" + warp.name + "§f to your current location.");
			}
			else if (command.equals("info")) {
				//warp <warp point name> info 
				Vector warpLocation = warp.location.toVector();

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

				long unitsFromYou = Math.round(warpLocation.distance(ply.getLocation().toVector()));
				long unitsFromSpawn = Math.round(warpLocation.distance(ply.getWorld().getSpawnLocation().toVector()));

				playerHelper.SendDirectedMessage(
						ply, "This warp is " +
						unitsFromYou + "m from you and " +
						unitsFromSpawn + "m from the spawn.");
			}
			else if (command.equals("remove")) {
				plugin.warpEngine.removeWarp(playerName, warp.name);
				playerHelper.SendDirectedMessage(ply, "Removed warp §9" + warp.name + "§f.");
			}
			else {
				throw new WarpException("Unknown /warp command.");
			}
			plugin.warpEngine.SaveWarps();
		}
		catch (ArrayIndexOutOfBoundsException e) {
			playerHelper.SendDirectedMessage(ply, "Not enough arguments.");
		}
	}

	@Override
	public String GetHelp() {
		return "Teleports you to the specified warp point.";
	}

	@Override
	public String GetUsage() {
		return "<warp point name>|+ <command>[ <args>] - see /cwarp";
	}
}
