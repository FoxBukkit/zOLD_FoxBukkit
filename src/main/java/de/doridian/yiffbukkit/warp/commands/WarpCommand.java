package de.doridian.yiffbukkit.warp.commands;

import de.doridian.yiffbukkit.core.util.PlayerHelper;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;
import de.doridian.yiffbukkit.warp.WarpDescriptor;
import de.doridian.yiffbukkit.warp.WarpException;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.UUID;

@Names("warp")
@Help("Teleports you to the specified warp point.")
@Usage("<warp point name>|+ <command>[ <args>] - see /setwarp")
@Permission("yiffbukkit.warp.warp")
public class WarpCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		if (args.length == 0) {
			//warp
			final StringBuilder sb = new StringBuilder("Available warps: ");
			boolean first = true;

			final Collection<WarpDescriptor> values = plugin.warpEngine.getWarps().values();
			final WarpDescriptor[] valueArray = values.toArray(new WarpDescriptor[values.size()]);

			final Location location = getCommandSenderLocation(commandSender, false, null);
			if (location != null) {
				final Vector playerPos = location.toVector();
				Arrays.sort(valueArray, 0, valueArray.length, new Comparator<WarpDescriptor>() {
					public int compare(WarpDescriptor lhs, WarpDescriptor rhs) {
						return -Double.compare(lhs.location.toVector().distanceSquared(playerPos), rhs.location.toVector().distanceSquared(playerPos));
					}
				});
			}

			for (WarpDescriptor warp : valueArray) {
				if (warp.isHidden)
					continue;

				final int rank = warp.checkAccess(commandSender);
				if (rank < 1)
					continue;

				if (!first)
					sb.append(", ");

				if (rank == 2) { // TODO: use actual rank, not checkAccess
					sb.append("\u00a77@\u00a7f");
				}
				else if (rank >= 2) {
					sb.append("\u00a77#\u00a7f");
				}

				sb.append(warp.name);

				first = false;
			}

			PlayerHelper.sendDirectedMessage(commandSender, sb.toString());
			return;
		}

		if (args[0].equals("help")) {
			//warp help
			PlayerHelper.sendDirectedMessage(commandSender, "/warp <warp point name> [<command>[ <args>]]");
			PlayerHelper.sendDirectedMessage(commandSender, "commands:");
			PlayerHelper.sendDirectedMessage(commandSender, "without arguments - teleport to warp");
			PlayerHelper.sendDirectedMessage(commandSender, "info - Shows information");
			PlayerHelper.sendDirectedMessage(commandSender, "changeowner <new owner> - Transfers ownership");
			PlayerHelper.sendDirectedMessage(commandSender, "public|private - Change public access");
			PlayerHelper.sendDirectedMessage(commandSender, "hide|show - Change warp visibility in warp list");
			PlayerHelper.sendDirectedMessage(commandSender, "addguest <name> - Grant guest access (can teleport)");
			PlayerHelper.sendDirectedMessage(commandSender, "addop <name> - Grant op access (can add guests)");
			PlayerHelper.sendDirectedMessage(commandSender, "deny <name> - Deny access");
			PlayerHelper.sendDirectedMessage(commandSender, "move - Move the warp to your current position");
			PlayerHelper.sendDirectedMessage(commandSender, "remove - Deletes the warp. This cannot be undone!");
			return;
		}

		try {
			final WarpDescriptor warp = plugin.warpEngine.getWarp(commandSender, args[0]);
			if (args.length == 1) {
				//warp <warp point name>
				if (playerHelper.isPlayerJailed(asPlayer(commandSender)))
					throw new YiffBukkitCommandException("You are jailed!");

				plugin.playerHelper.teleportWithHistory(asPlayer(commandSender), warp.location);
				return;
			}

			final String command = args[1].toLowerCase();

			final int rank = warp.checkAccess(commandSender);

			switch (command) {
			case "chown":
			case "changeowner":
				//warp <warp point name> changeowner <new owner>
				final UUID newOwnerName = playerHelper.matchPlayerSingle(args[2]).getUniqueId();
				if (newOwnerName == null)
					throw new WarpException("No unique player found for '" + args[2] + "'");

				warp.setOwner(commandSender, newOwnerName);

				PlayerHelper.sendDirectedMessage(commandSender, "Transferred ownership of warp \u00a79" + warp.name + "\u00a7f to " + newOwnerName + ".");
				break;

			case "hide":
				//warp <warp point name> public
				if (rank < 3)
					throw new WarpException("Permission denied");

				warp.isHidden = true;

				PlayerHelper.sendDirectedMessage(commandSender, "Hiding warp \u00a79" + warp.name + "\u00a7f in warp list.");
				break;

			case "show":
			case "unhide":
				//warp <warp point name> public
				if (rank < 3)
					throw new WarpException("Permission denied");

				warp.isHidden = true;

				PlayerHelper.sendDirectedMessage(commandSender, "Showing warp \u00a79" + warp.name + "\u00a7f in warp list.");
				break;

			case "public":
			case "unlock":
				//warp <warp point name> public
				if (rank < 2)
					throw new WarpException("Permission denied");

				warp.isPublic = true;

				PlayerHelper.sendDirectedMessage(commandSender, "Set warp \u00a79" + warp.name + "\u00a7f to public.");
				break;

			case "private":
			case "lock":
				//warp <warp point name> private
				warp.isPublic = false;

				PlayerHelper.sendDirectedMessage(commandSender, "Set warp \u00a79" + warp.name + "\u00a7f to private.");
				break;

			case "deny": {
				//warp <warp point name> deny <name>
				final Player target = playerHelper.matchPlayerSingle(args[2], false);

				warp.setAccess(commandSender, target, 0);

				PlayerHelper.sendDirectedMessage(commandSender, "Revoked " + target.getName() + "'s access to warp \u00a79" + warp.name + "\u00a7f.");
				break;
			}
			case "addguest": {
				//warp <warp point name> addguest <name>
				final Player target = playerHelper.matchPlayerSingle(args[2], false);

				warp.setAccess(commandSender, target, 1);

				PlayerHelper.sendDirectedMessage(commandSender, "Granted " + target.getName() + " guest access to warp \u00a79" + warp.name + "\u00a7f.");
				break;
			}
			case "addop": {
				//warp <warp point name> addop <name>
				final Player target = playerHelper.matchPlayerSingle(args[2], false);

				warp.setAccess(commandSender, target, 2);

				PlayerHelper.sendDirectedMessage(commandSender, "Granted " + target.getName() + " op access to warp \u00a79" + warp.name + "\u00a7f.");
				break;
			}
			case "move":
				//warp <warp point name> move
				if (rank < 3)
					throw new WarpException("You need to be the warp's owner to do this.");

				warp.location = getCommandSenderLocation(commandSender, false).clone();

				PlayerHelper.sendDirectedMessage(commandSender, "Moved warp \u00a79" + warp.name + "\u00a7f to your current location.");
				break;

			case "info":
				//warp <warp point name> info
				final Vector warpPosition = warp.location.toVector();

				PlayerHelper.sendDirectedMessage(commandSender, "Warp \u00a79" + warp.name + "\u00a7f is owned by " + warp.getOwner());
				if (warp.isPublic)
					PlayerHelper.sendDirectedMessage(commandSender, "Warp is public");
				else
					PlayerHelper.sendDirectedMessage(commandSender, "Warp is private");

				final StringBuilder sb = new StringBuilder("Access list: ");
				boolean first = true;
				for (Entry<UUID, Integer> entry : warp.getRanks().entrySet()) {
					if (!first)
						sb.append(", ");

					if (entry.getValue() >= 2)
						sb.append('@');

					sb.append(playerHelper.getPlayerByUUID(entry.getKey()).getName());

					first = false;
				}
				PlayerHelper.sendDirectedMessage(commandSender, sb.toString());

				String msg = "This warp is ";

				final Location location = getCommandSenderLocation(commandSender, false, null);
				if (location != null) {
					final long unitsFromYou = Math.round(warpPosition.distance(location.toVector()));
					msg += unitsFromYou + "m from you and ";
				}

				final long unitsFromSpawn = Math.round(warpPosition.distance(warp.location.getWorld().getSpawnLocation().toVector()));
				msg += unitsFromSpawn + "m from the spawn.";

				PlayerHelper.sendDirectedMessage(commandSender, msg);
				break;

			case "remove":
				plugin.warpEngine.removeWarp(commandSender, warp.name);
				PlayerHelper.sendDirectedMessage(commandSender, "Removed warp \u00a79" + warp.name + "\u00a7f.");
				break;

			default:
				throw new WarpException("Unknown /warp command.");
			}

			plugin.warpEngine.SaveWarps();
		}
		catch (ArrayIndexOutOfBoundsException e) {
			PlayerHelper.sendDirectedMessage(commandSender, "Not enough arguments.");
		}
	}
}
