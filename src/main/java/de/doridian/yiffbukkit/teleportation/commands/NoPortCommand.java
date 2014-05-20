/**
 * This file is part of YiffBukkit.
 *
 * YiffBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * YiffBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with YiffBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
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
import java.util.UUID;

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
	public void Run(Player player, String[] args, String argStr, String commandName) throws YiffBukkitCommandException {
		boolean newState;
		final UUID playerName = player.getUniqueId();

		final String subCommand = args.length >= 1 ? args[0] : "";
		final UUID otherName = args.length >= 2 ? playerHelper.matchPlayerSingle(args[1]).getUniqueId() : null;

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
				newState = !summonPermissions.contains(playerName.toString());
			}
			else if (summonPermissions == null) {
				// /notp
				newState = !tpPermissions.contains(playerName.toString());
			}
			else if (tpPermissions.contains(playerName.toString()) == summonPermissions.contains(playerName.toString())) {
				// /noport, states of notp and nosummon are the same
				newState = !tpPermissions.contains(playerName.toString());
			}
			else {
				// /noport, states differ
				throw new YiffBukkitCommandException("The states of notp and nosummon differ. Please use /noport on/off explicitly.");
			}
			break;

		case "allow":
		case "accept":
			setException(player, args, playerName, otherName, true);
			return;

		case "deny":
		case "reject":
		case "revoke":
		case "forbid":
			setException(player, args, playerName, otherName, false);
			return;

		case "list":
			final Collection<UUID> otherNames = getExceptions(playerName);

			MessageHelper.sendMessage(player, String.format("Players that you allowed %s:", what()));
			for (UUID uuid : otherNames) {
				Player playerUUID = playerHelper.getPlayerByUUID(uuid);
				final String removeCommand = String.format("/%s deny %s", getNames()[0], playerUUID.getName());
				MessageHelper.sendMessage(player, MessageHelper.format(uuid) + " " + MessageHelper.button(removeCommand, "x", "red", true));
			}
			return;

		default:
			throw new YiffBukkitCommandException("Usage: " + getUsage());
		}

		if (tpPermissions != null) {
			if (newState)
				tpPermissions.add(playerName.toString());
			else
				tpPermissions.remove(playerName.toString());
		}

		if (summonPermissions != null) {
			if (newState)
				summonPermissions.add(playerName.toString());
			else
				summonPermissions.remove(playerName.toString());
		}
		playerHelper.savePortPermissions();

		PlayerHelper.sendDirectedMessage(player, getStateName(!newState) + " " + what() + ".");
	}

	private void setException(Player player, String[] args, UUID playerName, UUID otherName, boolean allow) throws YiffBukkitCommandException {
		if (args.length < 2)
			throw new YiffBukkitCommandException("Usage: " + getUsage());

		if (otherName == null)
			throw new YiffBukkitCommandException("Sorry, multiple players found!");

		setException(playerName, otherName, allow);
		final String undoCommand = String.format("/%s %s \"%s\"", getNames()[0], allow ? "deny" : "allow", otherName);
		MessageHelper.sendMessage(player, String.format("%s %s for %s. " + MessageHelper.button(undoCommand, "undo", "blue", false), getStateName(allow), what(), MessageHelper.format(otherName)));
	}

	private String getStateName(boolean allowed) {
		return allowed ? "Allowed" : "Disallowed";
	}

	private void setException(UUID playerName, UUID otherName, boolean newState) {
		final String pair = playerName.toString()+" "+otherName.toString();

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

	private Collection<UUID> getExceptions(final UUID playerName) throws YiffBukkitCommandException {
		if (tpPermissions != null && summonPermissions != null)
			throw new YiffBukkitCommandException("Usage: " + getUsage());

		final Set<String> permissions = tpPermissions == null ? summonPermissions : tpPermissions;

		return Collections2.transform(Collections2.filter(permissions, new Predicate<String>() {
			@Override
			public boolean apply(String s) {
				return s.startsWith(playerName.toString() + " ");
			}
		}), new Function<String, UUID>() {
			@Override
			public UUID apply(String s) {
				return UUID.fromString(s.substring(s.indexOf(' ') + 1));
			}
		});
	}

	protected String what() {
		return "teleportation and summoning";
	}
}
