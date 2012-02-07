package de.doridian.yiffbukkit.teleportation.commands;

import de.doridian.yiffbukkit.main.commands.ICommand;
import de.doridian.yiffbukkit.main.commands.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.ICommand.Usage;
import org.bukkit.entity.Player;

import java.util.Set;

@Names("noport")
@Help("Prevents teleportation and summoning or grants/revokes exceptions.")
@Usage("[on|off|allow <name>|deny <name>]")
@Permission("yiffbukkitsplit.teleport.noport.noport")
public class NoPortCommand extends ICommand {
	protected Set<String> tpPermissions;
	protected Set<String> summonPermissions;

	public NoPortCommand() {
		tpPermissions = playerHelper.playerTpPermissions;
		summonPermissions = playerHelper.playerSummonPermissions;
	}

	@Override
	public void Run(Player ply, String[] args, String argStr) {
		boolean newState;
		String playerName = ply.getName();

		String arg0 = args.length >= 1 ? args[0] : "";

		if (argStr.equals("on") || argStr.equals("1")) {
			newState = true;
		}
		else if (argStr.equals("off") || argStr.equals("0")) {
			newState = false;
		}
		else if (arg0.equals("allow") || arg0.equals("accept")) {
			if (args.length < 2) {
				playerHelper.sendDirectedMessage(ply, "Usage: " + getUsage());
				return;
			}

			String otherName = playerHelper.completePlayerName(args[1], true);
			if (otherName == null) {
				playerHelper.sendDirectedMessage(ply, "Sorry, multiple players found!");
			}
			else {
				setException(playerName, otherName, true);
				playerHelper.sendDirectedMessage(ply, "Allowed "+what()+" for "+otherName+".");
			}
			return;
		}
		else if (arg0.equals("deny") || arg0.equals("reject") || arg0.equals("revoke") || arg0.equals("forbid")) {
			if (args.length < 2) {
				playerHelper.sendDirectedMessage(ply, "Usage: " + getUsage());
				return;
			}

			String otherName = playerHelper.completePlayerName(args[1], true);
			if (otherName == null) {
				playerHelper.sendDirectedMessage(ply, "Sorry, multiple players found!");
			}
			else {
				setException(playerName, otherName, false);
				playerHelper.sendDirectedMessage(ply, "Disallowed "+what()+" for "+otherName+".");
			}
			return;
		}
		else if (argStr.isEmpty()) {
			// toggle
			if (tpPermissions == null) {
				newState = !summonPermissions.contains(playerName);
			}
			else if (summonPermissions == null || tpPermissions.contains(playerName) == summonPermissions.contains(playerName)) {
				newState = !tpPermissions.contains(playerName);
			}
			else {
				playerHelper.sendDirectedMessage(ply, "The states of notp and nosummon differ. Please use !noport on/off explicitly.");
				return;
			}
		}
		else {
			playerHelper.sendDirectedMessage(ply, "Usage: " + getUsage());
			return;
		}

		if (tpPermissions != null) {
			if (newState)
				tpPermissions.add(playerName);
			else
				tpPermissions.remove(playerName);
		}

		if (summonPermissions != null) {
			if (newState)
				summonPermissions.add(playerName);
			else
				summonPermissions.remove(playerName);
		}
		playerHelper.savePortPermissions();

		playerHelper.sendDirectedMessage(ply, (newState ? "Disallowed" : "Allowed")+" "+what()+".");
	}

	private void setException(String playerName, String otherName, boolean newState) {
		String pair = playerName+" "+otherName;

		if (tpPermissions != null) {
			if (newState)
				tpPermissions.add(pair);
			else
				tpPermissions.remove(pair);
		}
		if (summonPermissions != null) {
			if (newState)
				summonPermissions.add(pair);
			else
				summonPermissions.remove(pair);
		}
		playerHelper.savePortPermissions();
	}

	protected String what() {
		return "teleportation and summoning";
	}
}
