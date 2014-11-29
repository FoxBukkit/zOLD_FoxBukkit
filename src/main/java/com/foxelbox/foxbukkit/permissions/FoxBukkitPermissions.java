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
package com.foxelbox.foxbukkit.permissions;

import com.foxelbox.foxbukkit.fun.commands.YiffCommand;
import com.sk89q.util.StringUtil;
import com.foxelbox.foxbukkit.core.FoxBukkit;
import com.foxelbox.foxbukkit.permissions.listeners.PermissionPlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.LinkedHashSet;
import java.util.Set;

public class FoxBukkitPermissions {
	private static boolean coEnabled = true;

	public static void init() {
		new PermissionPlayerListener();

		load();
	}

	public static void load() {
		coEnabled = FoxBukkit.instance.configuration.getValue("checkoff-enabled", "true").equalsIgnoreCase("true");
		checkOffPlayers.clear();

		if(!coEnabled)
			return;

		try {
			final File file = new File(FoxBukkit.instance.getDataFolder(), "coplayers.txt");
			if (!file.exists())
				return;
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while((line = reader.readLine()) != null) {
				final String playerName = line.toLowerCase();
				checkOffPlayers.add(playerName);
				refreshCOPlayerOnlineState(playerName);
			}
			reader.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static Set<String> checkOffPlayers = new LinkedHashSet<>();

	/**
	 * Adds a player to checkoff.
	 *
	 * @param player the player to add
	 * @return true if the player wasn't already on checkoff
	 */
	public static boolean addCOPlayer(Player player) {
		return addCOPlayer(player.getName());
	}

	/**
	 * Adds a player to checkoff.
	 *
	 * @param playerName the name of the player to add
	 * @return true if the player wasn't already on checkoff
	 */
	public static boolean addCOPlayer(String playerName) {
		if(!coEnabled)
			return false;

		playerName = playerName.toLowerCase();
		if(checkOffPlayers.contains(playerName))
			return false;

		checkOffPlayers.add(playerName);
		saveCO();

		refreshCOPlayerOnlineState(playerName);

		return true;
	}

	/**
	 * Removes a player from checkoff.
	 *
	 * @param player the player to remove
	 * @return true if the player wasn't already on checkoff
	 */
	public static boolean removeCOPlayer(Player player) {
		return removeCOPlayer(player.getName());
	}

	/**
	 * Removes a player from checkoff.
	 *
	 * @param playerName the name of the player to remove
	 * @return true if the player wasn't already on checkoff
	 */
	public static boolean removeCOPlayer(String playerName) {
		if(!coEnabled)
			return false;

		playerName = playerName.toLowerCase();
		if (!checkOffPlayers.contains(playerName))
			return false;

		checkOffPlayers.remove(playerName);
		saveCO();

		board.resetScores(getOfflinePlayer(playerName, true));
		board.resetScores(getOfflinePlayer(playerName, false));

		return true;
	}

	private static void saveCO() {
		if(!coEnabled)
			return;

		try {
			PrintWriter writer = new PrintWriter(new FileWriter(new File(FoxBukkit.instance.getDataFolder(), "coplayers.txt")));
			String[] plys = checkOffPlayers.toArray(new String[checkOffPlayers.size()]);
			for(String ply : plys) {
				writer.println(ply.toLowerCase());
			}
			writer.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static final String DUMMY_CRITERION = "dummy";
	private static final Scoreboard board = FoxBukkit.instance.playerHelper.createFBScoreboard();
	private static final Objective objective = board.registerNewObjective("checkoff", DUMMY_CRITERION);
	static {
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName("Checkov");
	}

	// CO online status update

	/**
	 * Refreshes the player's online status if they're on checkoff.
	 *
	 * @param playerName the name of the player to refresh
	 */
	public static void refreshCOPlayerOnlineState(String playerName) {
		if(!coEnabled)
			return;

		setCOPlayerOnlineState(playerName, Bukkit.getOfflinePlayer(playerName).isOnline());
	}

	/**
	 * Sets the player's online status if they're on checkoff.
	 *
	 * @param playerName the name of the player to refresh
	 * @param online the new online status
	 */
	public static void setCOPlayerOnlineState(String playerName, boolean online) {
		if(!coEnabled)
			return;

		playerName = playerName.toLowerCase();

		if(!checkOffPlayers.contains(playerName))
			return;

		board.resetScores(getOfflinePlayer(playerName, !online));
		final Score score = objective.getScore(getOfflinePlayer(playerName, online));
		if (online) {
			score.setScore(1);
			score.setScore(0);
		}
		else {
			score.setScore(1);
		}
	}

	// CO display

	/**
	 * Toggle checkoff display for the specified player
	 *
	 * @param player the player to toggle for.
	 * @return new state
	 */
	public static boolean toggleDisplayCO(Player player) {
		if(!coEnabled)
			return false;

		if (isDisplayingCO(player)) {
			player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
			return false;
		}
		else {
			player.setScoreboard(board);
			return true;
		}
	}

	/**
	 * Return whether checkoff is display for the specified player
	 *
	 * @param player the player to query.
	 * @return current state
	 */
	public static boolean isDisplayingCO(Player player) {
		if(!coEnabled)
			return false;

		return player.getScoreboard() == board;
	}

	private static OfflinePlayer getOfflinePlayer(String playerName, boolean online) {
		final String text = StringUtil.trimLength((online ? "\u00a72" : "\u00a7c") + playerName, 16);
		return Bukkit.getOfflinePlayer(text);
	}
}
