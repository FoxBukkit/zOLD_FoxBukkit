/**
 * This file is part of FoxBukkit.
 *
 * FoxBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoxBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FoxBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.doridian.foxbukkit.permissions.commands;

import de.doridian.foxbukkit.core.util.MessageHelper;
import de.doridian.foxbukkit.core.util.PlayerHelper;
import de.doridian.foxbukkit.main.FoxBukkitCommandException;
import de.doridian.foxbukkit.main.commands.system.ICommand;
import de.doridian.foxbukkit.main.commands.system.ICommand.BooleanFlags;
import de.doridian.foxbukkit.main.commands.system.ICommand.Help;
import de.doridian.foxbukkit.main.commands.system.ICommand.Names;
import de.doridian.foxbukkit.main.commands.system.ICommand.Permission;
import de.doridian.foxbukkit.main.commands.system.ICommand.Usage;
import de.doridian.foxbukkit.permissions.FoxBukkitPermissions;
import org.bukkit.entity.Player;

@Names({"checkoff","co"})
@Help("Check-Off list and system for FB")
@Usage("[[-f|-u|] name|-l|on|off]")
@BooleanFlags("ful")
@Permission("foxbukkit.checkoff")
public class CheckOffCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr, String commandName) throws FoxBukkitCommandException {
		args = parseFlags(args);
		if (booleanFlags.contains('l')) {
			for (String playerName : FoxBukkitPermissions.checkOffPlayers) {
				final String color = isOnline(playerName) ? MessageHelper.ONLINE_COLOR : MessageHelper.OFFLINE_COLOR;
				MessageHelper.sendMessage(ply, String.format("<color name=\"%1$s\">%2$s</color>", color, playerName) + " " + getButtonsForPlayer(playerName));
			}
			return;
		}

		switch (args.length) {
		case 0:
			if (FoxBukkitPermissions.toggleDisplayCO(ply)) {
				PlayerHelper.sendDirectedMessage(ply, "Enabled CO display");
			} else {
				PlayerHelper.sendDirectedMessage(ply, "Disabled CO display");
			}
			return;

		case 1:
			switch (args[0]) {
			case "on":
				if (FoxBukkitPermissions.isDisplayingCO(ply)) {
					PlayerHelper.sendDirectedMessage(ply, "CO display already enabled");
				}
				else {
					FoxBukkitPermissions.toggleDisplayCO(ply);
					PlayerHelper.sendDirectedMessage(ply, "Enabled CO display");
				}
				return;

			case "off":
				if (FoxBukkitPermissions.isDisplayingCO(ply)) {
					FoxBukkitPermissions.toggleDisplayCO(ply);
					PlayerHelper.sendDirectedMessage(ply, "Disabled CO display");
				}
				else {
					PlayerHelper.sendDirectedMessage(ply, "CO display already disabled");
				}
				return;
			}
		}

		final String playerName = args[0];
		if (booleanFlags.contains('u')) {
			if (FoxBukkitPermissions.addCOPlayer(playerName)) {
				MessageHelper.sendMessage(ply, "Added player " + playerName + " to CO. " + getButtonsForPlayer(playerName));
			} else {
				PlayerHelper.sendDirectedMessage(ply, "Player "+playerName+" already on CO");
			}
			return;
		}

		if (!booleanFlags.contains('f') && isOnline(playerName))
			throw new FoxBukkitCommandException("Cannot check off online player without -f flag.");

		if (FoxBukkitPermissions.removeCOPlayer(playerName)) {
			MessageHelper.sendMessage(ply, "Removed player %1$s from CO. " + MessageHelper.button("/co -u " + playerName, "undo", "dark_green", true), playerName);
		} else {
			PlayerHelper.sendDirectedMessage(ply, "Player "+playerName+" not found on CO");
		}
	}

	private String getButtonsForPlayer(String playerName) {
		String buttons = MessageHelper.button("/lb player " + playerName + " sum blocks", "lb", "blue", true) + " "
					   + MessageHelper.button("/lb player " + playerName + " chestaccess", "chest", "blue", true);
		if (isOnline(playerName)) {
			buttons = MessageHelper.button("/at 0 vanish on; tp -sn \"" + playerName + '"', "tp", "blue", true) + " " + buttons;
		} else {
			buttons = MessageHelper.button("/co " + playerName, "x", "red", true) + " " + buttons;
		}
		return buttons;
	}

	private boolean isOnline(String playerName) {
		Player plyply = plugin.getServer().getPlayerExact(playerName);
		//noinspection SimplifiableIfStatement
		if (plyply == null)
			return false;

		return plyply.isOnline();
	}
}
