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
package com.foxelbox.foxbukkit.warp.listeners;

import com.foxelbox.foxbukkit.core.util.AutoCleanup;
import com.foxelbox.foxbukkit.core.util.PlayerHelper;
import com.foxelbox.foxbukkit.main.listeners.BaseListener;
import com.foxelbox.foxbukkit.warp.WarpDescriptor;
import com.foxelbox.foxbukkit.warp.WarpException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.vehicle.VehicleUpdateEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class SignPortalPlayerListener extends BaseListener {
	public SignPortalPlayerListener() {
		AutoCleanup.registerPlayerMap(lastTouchedPortal);
		AutoCleanup.registerPlayerSet(portalStates);
	}

	private static final BlockFace[] faces = { BlockFace.NORTH,BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN };

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() != Action.LEFT_CLICK_BLOCK || event.getItem() == null || event.getItem().getTypeId() != 323)
			return;

		final BlockState blockState = event.getClickedBlock().getState();
		if (!(blockState instanceof Sign))
			return;

		final Sign sign = (Sign) blockState;
		if (!sign.getLine(0).trim().equalsIgnoreCase("[Portal]"))
			return;

		final Player player = event.getPlayer();
		try {
			final WarpDescriptor warpDescriptor = plugin.warpEngine.getWarp(player, sign.getLine(1));
			sign.setLine(0, "\u00a79[Portal]");
			sign.setLine(1, warpDescriptor.name);
			if (!player.hasPermission("foxbukkit.signportal.public"))
				sign.setLine(2, "private");
			sign.setLine(3, player.getName());
			sign.update(true);
		} catch (WarpException e) {
			PlayerHelper.sendDirectedMessage(player, e.getMessage(), e.getColor());
			return;
		}

		PlayerHelper.sendDirectedMessage(player, "Portal sign activated.");
		event.setCancelled(true);
	}

	private final Map<Player, Block> lastTouchedPortal = new HashMap<>();
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerPortal(PlayerPortalEvent event) {
		final Player player = event.getPlayer();
		Block block = event.getTo().getBlock();
		if (block.getType() != Material.PORTAL)
			block = lastTouchedPortal.get(player);

		if (block.getType() != Material.PORTAL)
			return;

		if (doPort(player, player, block))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityPortalEnter(EntityPortalEnterEvent event) {
		final Entity entity = event.getEntity();
		if (!(entity instanceof Player))
			return;

		lastTouchedPortal.put((Player) entity, event.getLocation().getBlock());
	}


	private final Set<Player> portalStates = new HashSet<>();
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onVehicleUpdate(VehicleUpdateEvent event) {
		final Vehicle vehicle = event.getVehicle();

		final Entity passenger = vehicle.getPassenger();
		if (!(passenger instanceof Player))
			return;

		final Player player = (Player)vehicle.getPassenger();

		final Location location = vehicle.getLocation();
		final boolean isPortal = vehicle.getWorld().getBlockTypeIdAt(location) == 90;
		final boolean wasPortal = portalStates.contains(player);
		if (wasPortal == isPortal)
			return;

		if (isPortal) {
			final Block block = location.getBlock();

			doPort(player, vehicle, block);
		}
	}

	/**
	 * Teleports a player and, optionally, a vehicle, to the sign portal's destination, if there is one.
	 *
	 * @param player The player to teleport
	 * @param entityToPort An additional vehicle to port and reattach to the player
	 * @param block a block that's part of the portal
	 * @return <code>true</code> if the block belongs to a sign portal, <code>false</code> otherwise
	 */
	private boolean doPort(final Player player, final Entity entityToPort, final Block block) {
		final Stack<Block> todo = new Stack<>();
		todo.push(block);

		final Set<Block> portalsAndNeighboring = new HashSet<>();
		while (!todo.isEmpty()) {
			final Block current = todo.pop();

			final int typeId = current.getTypeId();
			if (typeId == 0)
				continue;

			if (!portalsAndNeighboring.add(current))
				continue;

			if (typeId != 90)
				continue;

			for (BlockFace face : faces) {
				todo.push(current.getRelative(face));
			}
		}

		for (Block current : portalsAndNeighboring) {
			for (BlockFace face : faces) {
				final Block attached = current.getRelative(face);
				int typeId = attached.getTypeId();
				if (typeId != 63 && typeId != 68)
					continue;

				final String[] lines = ((Sign)attached.getState()).getLines();

				if (!lines[0].equals("\u00a79[Portal]"))
					continue;

				final WarpDescriptor warpDescriptor;
				final String warpName = lines[1];
				if (lines[2].equalsIgnoreCase("private")) {
					try {
						warpDescriptor = plugin.warpEngine.getWarp(player, warpName);
					} catch (WarpException e) {
						player.sendMessage("\u00a7cYour entrance is blocked by a powerful entity.");
						return true;
					}
				}
				else {
					warpDescriptor = plugin.warpEngine.getWarps().get(warpName.toLowerCase());
				}

				// TODO: y offset when porting?
				final String portalOwnerName = lines[3];
				final CommandSender portalOwner = playerHelper.literalMatch(portalOwnerName);
				if (warpDescriptor.checkAccess(portalOwner) < 1) {
					player.sendMessage("\u00a7cThe arcane forces no longer bind this portal to its target.");
					return true;
				}

				if (player != entityToPort) {
					final Class<? extends Entity> clazz = entityToPort.getClass();
					player.leaveVehicle();
					entityToPort.remove();
					player.teleport(warpDescriptor.location);
					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() { public void run() {
						Entity newEntityToPort = warpDescriptor.location.getWorld().spawn(warpDescriptor.location, clazz);
						newEntityToPort.setPassenger(player);
					}});
				}
				else {
					entityToPort.teleport(warpDescriptor.location);
				}

				player.sendMessage("\u00a79You're hurtled through the ethereal realm to your destination.");
				return true;
			}
		}

		return false;
	}
}
