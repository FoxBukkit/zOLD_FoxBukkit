package de.doridian.yiffbukkit.teleportation.commands;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import de.doridian.yiffbukkit.core.util.MessageHelper;
import de.doridian.yiffbukkit.core.util.PlayerHelper;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Set;

@Names("noport")
@Help("Prevents teleportation and summoning or grants/revokes exceptions.")
@Usage("[on|off|allow <name>|deny <name>]")
@Permission("yiffbukkit.teleport.noport.noport")
public class NoPortCommand extends ICommand {
	protected Set<String> tpPermissions;
	protected Set<String> summonPermissions;

	public NoPortCommand() {
		tpPermissions = playerHelper.playerTpPermissions;
		summonPermissions = playerHelper.playerSummonPermissions;
	}

	@Override
	public void Run(Player player, String[] args, String argStr) throws YiffBukkitCommandException {
		boolean newState;
		final String playerName = player.getName();

		final String subCommand = args.length >= 1 ? args[0] : "";
		final String otherName = args.length >= 2 ? playerHelper.completePlayerName(args[1], true) : null;

		switch (subCommand) {
		case "on":
		case "1":
			newState = true;
			break;

		case "off":
		case "0":
			newState = false;
			break;

		case "":
			// toggle
			if (tpPermissions == null) {
				// /nosummon
				newState = !summonPermissions.contains(playerName);
			}
			else if (summonPermissions == null) {
				// /notp
				newState = !tpPermissions.contains(playerName);
			}
			else if (tpPermissions.contains(playerName) == summonPermissions.contains(playerName)) {
				// /noport, states of notp and nosummon are the same
				newState = !tpPermissions.contains(playerName);
			}
			else {
				// /noport, states differ
				throw new YiffBukkitCommandException("The states of notp and nosummon differ. Please use /noport on/off explicitly.");
			}
			break;

		case "allow":
		case "accept":
			if (args.length < 2)
				throw new YiffBukkitCommandException("Usage: " + getUsage());

			if (otherName == null)
				throw new YiffBukkitCommandException("Sorry, multiple players found!");

			setException(playerName, otherName, true);
			PlayerHelper.sendDirectedMessage(player, "Allowed " + what() + " for " + otherName + ".");
			return;

		case "deny":
		case "reject":
		case "revoke":
		case "forbid":
			if (args.length < 2) {
				PlayerHelper.sendDirectedMessage(player, "Usage: " + getUsage());
				return;
			}

			if (otherName == null) {
				PlayerHelper.sendDirectedMessage(player, "Sorry, multiple players found!");
			}
			else {
				setException(playerName, otherName, false);
				PlayerHelper.sendDirectedMessage(player, "Disallowed " + what() + " for " + otherName + ".");
			}
			return;

		case "list":
			final Collection<String> otherNames = getExceptions(playerName);

			for (String name : otherNames) {
				final String removeCommand = String.format("/%s deny %s", getNames()[0], name);
				MessageHelper.sendMessage(player, name + " " + MessageHelper.button(removeCommand, "x", "red", true));
			}
			return;

		default:
			throw new YiffBukkitCommandException("Usage: " + getUsage());
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

		PlayerHelper.sendDirectedMessage(player, (newState ? "Disallowed" : "Allowed") + " " + what() + ".");
	}

	private void setException(String playerName, String otherName, boolean newState) {
		final String pair = playerName+" "+otherName;

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

	private Collection<String> getExceptions(final String playerName) throws YiffBukkitCommandException {
		if (tpPermissions != null && summonPermissions != null)
			throw new YiffBukkitCommandException("Usage: " + getUsage());

		final Set<String> permissions = tpPermissions == null ? summonPermissions : tpPermissions;

		return Collections2.transform(Collections2.filter(permissions, new Predicate<String>() {
			@Override
			public boolean apply(String s) {
				return s.startsWith(playerName + " ");
			}
		}), new Function<String, String>() {
			@Override
			public String apply(String s) {
				return s.substring(s.indexOf(' ') + 1);
			}
		});
	}

	protected String what() {
		return "teleportation and summoning";
	}
}
