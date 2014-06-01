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
package de.doridian.foxbukkit.core.util;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import de.doridian.foxbukkit.core.FoxBukkit;
import de.doridian.foxbukkit.main.StateContainer;
import de.doridian.foxbukkit.main.chat.Parser;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static de.doridian.foxbukkit.main.chat.Parser.escape;

public class MessageHelper extends StateContainer {
	private static final String PLAYER_FORMAT = "<span onClick=\"suggest_command('/pm %1$s ')\"%3$s>%2$s</span>";

	private static final String FB_DEFAULT_COLOR = "dark_purple";
	private static final String FB_ERROR_COLOR = "dark_red";

	public static final String ONLINE_COLOR = "dark_green";
	public static final String OFFLINE_COLOR = "dark_red";

	public static String format(CommandSender commandSender) {
		return format(commandSender.getUniqueId(), commandSender, false);
	}

	public static String formatWithTag(CommandSender commandSender) {
		return format(commandSender.getUniqueId(), commandSender, true);
	}

	public static String format(UUID uuid) {
		return format(uuid, Bukkit.getPlayer(uuid), false);
	}

	public static String formatWithTag(UUID uuid) {
		return format(uuid, Bukkit.getPlayer(uuid), true);
	}

	private static String format(UUID uuid, CommandSender commandSender, boolean withTag) {
		final String onHover;
		String displayName, name;
		if (commandSender == null) {
			commandSender = FoxBukkit.instance.playerHelper.getPlayerByUUID(uuid);
			onHover = "";
			displayName = commandSender.getName();
			name = commandSender.getName();
		}
		else {
			name = commandSender.getName();
			displayName = FoxBukkit.instance.playerHelper.getPlayerRankTag(uuid) + commandSender.getDisplayName();
			final String playerTag = FoxBukkit.instance.playerHelper.getPlayerTagRaw(uuid, false);
			if (withTag && playerTag != null) {
				displayName = playerTag + " " + displayName;
			}
			if (commandSender instanceof Player) {
				final Player player = (Player) commandSender;
				final String color = player.isOnline() ? ONLINE_COLOR : OFFLINE_COLOR;
				final String hoverText = String.format("<color name=\"%1$s\">%2$s</color>", color, commandSender.getName());
				onHover = " onHover=\"show_text('" + escape(hoverText) + "')\"";
			}
			else {
				onHover = "";
			}
		}
		return String.format(PLAYER_FORMAT, name, displayName, onHover);
	}

	public static String button(String command, String label, String color, boolean run) {
		final String eventType = run ? "run_command" : "suggest_command";
		return String.format("<color name=\"%3$s\" onClick=\"%4$s('%1$s')\" onHover=\"show_text('%1$s')\">[%2$s]</color>", escape(command), escape(label), escape(color), eventType);
	}

	public static void sendServerMessage(String format, Object... params) {
		sendColoredServerMessage(FB_DEFAULT_COLOR, format, params);
	}

	public static void sendColoredServerMessage(String color, String format, Object... params) {
		sendColoredServerMessage(color, Predicates.<Player>alwaysTrue(), format, params);
	}

	public static void sendServerMessage(Predicate<? super Player> predicate, String format, Object... params) {
		sendColoredServerMessage(FB_DEFAULT_COLOR, predicate, format, params);
	}

	public static void sendColoredServerMessage(String color, Predicate<? super Player> predicate, String format, Object... params) {
		if (color != null) {
			format = "<color name=\"" + color + "\">[FB]</color> " + format;
		}

		final Player[] players = Bukkit.getOnlinePlayers();
		final List<CommandSender> targetPlayers = new ArrayList<>();

		for (Player player : players) {
			if (!predicate.apply(player))
				continue;

			targetPlayers.add(player);
		}

		Parser.sendToPlayers(targetPlayers, format, params);
	}

	public static void sendMessage(CommandSender commandSender, String format, Object... params) {
		sendMessage(FB_DEFAULT_COLOR, commandSender, format, params);
	}

	public static void sendErrorMessage(CommandSender commandSender, String format, Object... params) {
		sendMessage(FB_ERROR_COLOR, commandSender, format, params);
	}

	public static void sendMessage(String color, CommandSender commandSender, String format, Object... params) {
		if (color != null) {
			format = "<color name=\"" + color + "\">[FB]</color> " + format;
		}

		Parser.sendToPlayer(commandSender, format, params);
	}
}
