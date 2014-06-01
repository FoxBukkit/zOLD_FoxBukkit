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
package com.foxelbox.foxbukkit.spectate;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;

public class SpectatePlayer {
	private static final HashMap<Player, SpectatePlayer> wrappedPlayers = new HashMap<>();

	private final Player player;

	private SpectatePlayer isSpectating = null;
	private SpectatePlayer isSpectatingCur = null;
	private HashSet<SpectatePlayer> spectatedBy = new HashSet<>();

	private ItemStack[] originalInventory = null;
	private ItemStack[] originalArmor = null;
	private Location originalPosition = null;
	private double originalHealth = 0;
	private int originalFood = 0;
	private float originalExp = 0;
	private int originalLevel = 0;
	private GameMode originalGameMode = null;

	private SpectatePlayer(Player original) {
		this.player = original;
		wrappedPlayers.put(original, this);
	}

	public static SpectatePlayer wrapPlayer(Player player) {
		if(wrappedPlayers.containsKey(player)) {
			return wrappedPlayers.get(player);
		}

		return new SpectatePlayer(player);
	}
	
	public static SpectatePlayer removeWrappedPlayer(Player player) {
		return wrappedPlayers.remove(player);
	}

	public void spectatePlayer(SpectatePlayer other) {
		unspectate();
		if(other == this) {
			player.sendMessage("You cannot spectate yourself!");
			return;
		}
		isSpectating = other;

		originalArmor = player.getInventory().getArmorContents();
		originalInventory = player.getInventory().getContents();
		originalHealth = player.getHealth();
		originalFood = player.getFoodLevel();
		originalExp = player.getExp();
		originalLevel = player.getLevel();
		originalPosition = player.getLocation();
		originalGameMode = player.getGameMode();

		refreshSpectatingCurAll();
		player.sendMessage("You are now spectating " + isSpectating.player.getName());
	}

	public static void refreshSpectatingCurAll() {
		for(SpectatePlayer ply : wrappedPlayers.values()) {
			ply.refreshSpectatingCur();
		}
	}

	public void refreshSpectatingCur() {
		SpectatePlayer oldSpectate = isSpectatingCur;
		if(isSpectating != null && !isSpectating()) {
			isSpectating = null;
			isSpectatingCur = null;
			player.sendMessage("The player you spectated went offline. Spectating aborted.");
			refreshSpectatingCurAll();
			return;
		}
		isSpectatingCur = isSpectating;
		if(isSpectatingCur == null) {
			if(oldSpectate != null) {
				oldSpectate.player.showPlayer(this.player);
				this.player.showPlayer(oldSpectate.player);
			}
			return;
		}

		while(isSpectatingCur.isSpectating != null) {
			if(isSpectatingCur == this) {
				player.sendMessage("Circular spectation detected D:");
				isSpectatingCur = null;
				return;
			}
			isSpectatingCur = isSpectatingCur.isSpectating;
		}

		for(SpectatePlayer player : wrappedPlayers.values()) {
			player.spectatedBy.remove(this);
		}
		isSpectatingCur.spectatedBy.add(this);

		if(oldSpectate == isSpectatingCur) return;

		if(oldSpectate != null) {
			oldSpectate.player.showPlayer(this.player);
			this.player.showPlayer(oldSpectate.player);
		}

		if(isSpectatingCur != null) {
			isSpectatingCur.player.hidePlayer(this.player);
			this.player.hidePlayer(isSpectatingCur.player);
		}

		refresh(true, true, true, true, true);
	}

	public boolean isSpectating() {
		return isSpectating != null && wrappedPlayers.containsValue(isSpectating) && isSpectating.player.isOnline();
	}

	public void unspectate() {
		if(isSpectating == null) return;

		player.sendMessage("You are no longer spectating " + isSpectating.player.getName());
		isSpectating = null;
		refreshSpectatingCurAll();

		player.getInventory().setArmorContents(originalArmor);
		player.getInventory().setContents(originalInventory);
		player.setHealth(originalHealth);
		player.setFoodLevel(originalFood);
		player.setExp(originalExp);
		player.setLevel(originalLevel);
		player.teleport(originalPosition);
		player.setGameMode(originalGameMode);

		originalArmor = null;
		originalInventory = null;
		originalHealth = 0;
		originalFood = 0;
		originalExp = 0;
		originalLevel = 0;
		originalPosition = null;
		originalGameMode = null;
	}

	public void refresh(boolean inventory, boolean exp, boolean location, boolean health, boolean food) {
		if(isSpectatingCur == null) return;

		if(inventory) {
			player.getInventory().setArmorContents(isSpectatingCur.player.getInventory().getArmorContents());
			player.getInventory().setContents(isSpectatingCur.player.getInventory().getContents());
			player.setGameMode(isSpectatingCur.player.getGameMode());
		}
		if(health) {
			player.setHealth(isSpectatingCur.player.getHealth());
		}
		if(food) {
			player.setFoodLevel(isSpectatingCur.player.getFoodLevel());
		}
		if(exp) {
			player.setExp(isSpectatingCur.player.getExp());
			player.setLevel(isSpectatingCur.player.getLevel());
		}
		if(location) {
			player.teleport(isSpectatingCur.player);
		}
	}

	public void refreshSpectators(boolean inventory, boolean exp, boolean location, boolean health, boolean food) {
		for(SpectatePlayer spectated : spectatedBy) {
			spectated.refresh(inventory, exp, location, health, food);
		}
	}

	public static void refreshAll(boolean inventory, boolean exp, boolean location, boolean health, boolean food) {
		for (SpectatePlayer player : SpectatePlayer.wrappedPlayers.values()) {
			player.refresh(inventory, exp, location, health, food);
		}
	}
}
