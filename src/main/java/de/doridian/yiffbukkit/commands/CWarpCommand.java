package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.warp.WarpDescriptor;
import de.doridian.yiffbukkit.warp.WarpException;

public class CWarpCommand extends ICommand {
	@Override
	public int GetMinLevel() {
		return 0;
	}

	public CWarpCommand(YiffBukkit plug) {
		super(plug);
	}

	@Override
	public void Run(Player ply, String[] args, String argStr) {
		if (argStr == "") {
			playerHelper.SendDirectedMessage(ply, "/cwarp <warp point name> <command>[ <args>] - commands:");
			playerHelper.SendDirectedMessage(ply, "changeowner <new owner> - Transfers ownership");
			playerHelper.SendDirectedMessage(ply, "public|private - Change public access");
			playerHelper.SendDirectedMessage(ply, "addguest <name> - Grant guest access (can teleport)");
			playerHelper.SendDirectedMessage(ply, "addop <name> - Grant op access (can add guests)");
			playerHelper.SendDirectedMessage(ply, "deny <name> - Deny access");
			playerHelper.SendDirectedMessage(ply, "move - Move the warp to your current position");
			return;
		}

		try {
			String command = args[1].toLowerCase();

			WarpDescriptor warp = plugin.warpEngine.getWarp(ply.getName(), args[0]);
			int rank = warp.checkAccess(ply.getName());

			if (command.equals("chown") || command.equals("changeowner")) {
				String newOwnerName = playerHelper.CompletePlayerName(args[2], false);
				warp.setOwner(ply.getName(), newOwnerName);

				playerHelper.SendDirectedMessage(ply, "Transferred ownership of warp §9" + warp.name + "§f to "+newOwnerName+".");
			}
			else if (command.equals("public") || command.equals("unlock")) {
				if (rank < 2)
					throw new WarpException("Permission denied");

				warp.isPublic = true;

				playerHelper.SendDirectedMessage(ply, "Set warp §9" + warp.name + "§f to public.");
			}
			else if (command.equals("private") || command.equals("lock")) {
				warp.isPublic = false;

				playerHelper.SendDirectedMessage(ply, "Set warp §9" + warp.name + "§f to private.");
			}
			else if (command.equals("deny")) {
				String targetName = playerHelper.CompletePlayerName(args[2], false);
				if (targetName == null)
					throw new WarpException("Player not found.");

				warp.setAccess(ply.getName(), targetName, 0);

				playerHelper.SendDirectedMessage(ply, "Revoked " + targetName + "'s access to warp §9" + warp.name + "§f.");
			}
			else if (command.equals("addguest")) {
				String targetName = playerHelper.CompletePlayerName(args[2], false);
				if (targetName == null)
					throw new WarpException("Player not found.");

				warp.setAccess(ply.getName(), targetName, 1);

				playerHelper.SendDirectedMessage(ply, "Granted " + targetName + " guest access to warp §9" + warp.name + "§f.");
			}
			else if (command.equals("addop")) {
				String targetName = playerHelper.CompletePlayerName(args[2], false);
				if (targetName == null)
					throw new WarpException("Player not found.");

				warp.setAccess(ply.getName(), targetName, 2);

				playerHelper.SendDirectedMessage(ply, "Granted " + targetName + " op access to warp §9" + warp.name + "§f.");
			}
			else if (command.equals("move")) {
				if (rank < 3)
					throw new WarpException("You need to be the warp's owner to do this.");

				warp.location = ply.getLocation().clone();

				playerHelper.SendDirectedMessage(ply, "Moved warp §9" + warp.name + "§f to your current location.");
			}
			else {
				throw new WarpException("Unknown /cwarp command.");
			}
			plugin.warpEngine.SaveWarps();
		}
		catch (WarpException e) {
			playerHelper.SendDirectedMessage(ply, e.getMessage());
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
