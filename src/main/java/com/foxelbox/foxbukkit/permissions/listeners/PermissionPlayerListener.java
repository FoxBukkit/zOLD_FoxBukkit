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
package com.foxelbox.foxbukkit.permissions.listeners;

import com.foxelbox.foxbukkit.core.FoxBukkit;
import com.foxelbox.foxbukkit.core.listeners.FoxBukkitBlockListener;
import com.foxelbox.foxbukkit.core.util.PlayerHelper;
import com.foxelbox.foxbukkit.main.listeners.BaseListener;
import com.foxelbox.foxbukkit.main.util.Utils;
import com.foxelbox.foxbukkit.permissions.FoxBukkitPermissibleBase;
import com.foxelbox.foxbukkit.permissions.FoxBukkitPermissions;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class PermissionPlayerListener extends BaseListener {
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		ItemStack item = event.getItemStack();
		Material itemMaterial = item.getType();
		if(itemMaterial == Material.AIR) return;

		Player ply = event.getPlayer();
		if(FoxBukkit.instance.playerHelper.isPlayerDisabled(ply)) {
			item.setType(Material.GOLD_HOE);
			item.setAmount(1);
			item.setDurability(Short.MAX_VALUE);
			return;
		}

		if (!ply.hasPermission("foxbukkit.place")) {
			PlayerHelper.sendServerMessage(ply.getName() + " is not allowed to build but tried tried to spawn " + itemMaterial + ".");
			item.setType(Material.GOLD_HOE);
			item.setAmount(1);
			item.setDurability(Short.MAX_VALUE);
			return;
		}

		final String permission = FoxBukkitBlockListener.blocklevels.get(itemMaterial);
		if (permission != null && !ply.hasPermission(permission)) {
			PlayerHelper.sendServerMessage(ply.getName() + " tried to spawn illegal block " + itemMaterial);
			item.setType(Material.GOLD_HOE);
			item.setAmount(1);
			item.setDurability(Short.MAX_VALUE);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		
		if(player instanceof CraftHumanEntity) {
			final CraftHumanEntity craftPlayer = (CraftHumanEntity)player;
			Utils.setPrivateValue(CraftHumanEntity.class, craftPlayer, "perm", new FoxBukkitPermissibleBase(player));
		} else {
			System.out.println("Sorry, invalid stuff :(");
		}

		FoxBukkitPermissions.setCOPlayerOnlineState(player.getName(), true);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerKick(PlayerKickEvent event) {
		final Player player = event.getPlayer();

		FoxBukkitPermissions.setCOPlayerOnlineState(player.getName(), false);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		final Player player = event.getPlayer();

		FoxBukkitPermissions.setCOPlayerOnlineState(player.getName(), false);
	}
}
