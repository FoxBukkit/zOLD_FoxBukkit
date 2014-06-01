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
package com.foxelbox.foxbukkit.spectate.listeners;

import com.foxelbox.foxbukkit.main.listeners.BaseListener;
import com.foxelbox.foxbukkit.spectate.SpectatePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SpectatePlayerListener extends BaseListener {
	public SpectatePlayerListener() {
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				SpectatePlayer.refreshAll(true, true, true, true, true);
			}
		}, 100, 0);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		SpectatePlayer.wrapPlayer(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		SpectatePlayer.wrapPlayer(event.getPlayer()).unspectate();
		SpectatePlayer.removeWrappedPlayer(event.getPlayer());
		SpectatePlayer.refreshSpectatingCurAll();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerMove(PlayerMoveEvent event) {
		SpectatePlayer player = SpectatePlayer.wrapPlayer(event.getPlayer());
		if (player.isSpectating()) {
			event.setCancelled(true);
			return;
		}

		player.refreshSpectators(false, false, true, false, false);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		SpectatePlayer player = SpectatePlayer.wrapPlayer(event.getPlayer());
		if(player.isSpectating()) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		SpectatePlayer player = SpectatePlayer.wrapPlayer(event.getPlayer());
		if(player.isSpectating()) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		SpectatePlayer player = SpectatePlayer.wrapPlayer(event.getPlayer());
		if(player.isSpectating()) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerExpChanged(PlayerExpChangeEvent event) {
		SpectatePlayer player = SpectatePlayer.wrapPlayer(event.getPlayer());
		if(player.isSpectating()) {
			event.setAmount(0);
			return;
		}
		player.refreshSpectators(false, true, false, false, false);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerHealthUp(EntityRegainHealthEvent event) {
		playerHealthChanged(event.getEntity(), event);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerHealthDown(EntityDamageEvent event) {
		playerHealthChanged(event.getEntity(), event);
	}

	private void playerHealthChanged(Entity ent, Cancellable event) {
		if (ent == null)
			return;

		if (!(ent instanceof Player))
			return;

		SpectatePlayer player = SpectatePlayer.wrapPlayer((Player) ent);
		if(player.isSpectating()) {
			event.setCancelled(true);
			return;
		}

		player.refreshSpectators(false, false, false, true, false);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerFoodChanged(FoodLevelChangeEvent event) {
		Entity ent = event.getEntity();
		if (ent == null)
			return;

		if (!(ent instanceof Player))
			return;

		SpectatePlayer player = SpectatePlayer.wrapPlayer((Player) ent);
		if(player.isSpectating()) {
			event.setCancelled(true);
			return;
		}

		player.refreshSpectators(false, false, false, false, true);
	}

	/*@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInventoryChanged(InventoryEvent event) {
		HumanEntity ent = event.getView().getPlayer();
		if(ent == null || !(ent instanceof Player)) return;
		SpectatePlayer player = SpectatePlayer.wrapPlayer((Player) ent);
		for(SpectatePlayer spectated : player.spectatedBy) {
			spectated.refresh(true, false, false, false, false);
		}
	}*/
}
