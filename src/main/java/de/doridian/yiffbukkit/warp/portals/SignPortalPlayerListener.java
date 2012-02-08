package de.doridian.yiffbukkit.warp.portals;

import de.doridian.yiffbukkit.warp.WarpDescriptor;
import de.doridian.yiffbukkit.warp.WarpException;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleUpdateEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class SignPortalPlayerListener implements Listener {
	final YiffBukkit plugin;

	public SignPortalPlayerListener(final YiffBukkit plugin) {
		this.plugin = plugin;

		plugin.playerHelper.registerMap(timerIds);
		plugin.playerHelper.registerSet(portalStates);

		plugin.getServer().getPluginManager().registerEvents(this, plugin);

	}

	private static final BlockFace[] faces = { BlockFace.NORTH,BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN };

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.isCancelled())
			return;

		if (event.getAction() != Action.LEFT_CLICK_BLOCK)
			return;

		if (event.getItem() == null)
			return;

		if (event.getItem().getTypeId() != 323)
			return;

		BlockState blockState = event.getClickedBlock().getState();
		if (!(blockState instanceof Sign))
			return;

		Sign sign = (Sign) blockState;
		if (!sign.getLine(0).trim().equalsIgnoreCase("[Portal]"))
			return;

		final Player player = event.getPlayer();
		try {
			final WarpDescriptor warpDescriptor = plugin.warpEngine.getWarp(player, sign.getLine(1));
			sign.setLine(0, "\u00a79[Portal]");
			sign.setLine(1, warpDescriptor.name);
			if (!plugin.permissionHandler.has(player, "yiffbukkitsplit.signportal.public"))
				sign.setLine(2, "private");
			sign.setLine(3, player.getName());
			sign.update(true);
		} catch (WarpException e) {
			plugin.playerHelper.sendDirectedMessage(player, e.getMessage(), e.getColor());
			return;
		}

		plugin.playerHelper.sendDirectedMessage(player, "Portal sign activated.");
	}

	Map<Player, Integer> timerIds = new HashMap<Player, Integer>();
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent event) {
		if (event.isCancelled())
			return;

		final boolean isPortal = event.getTo().getBlock().getTypeId() == 90;
		final boolean hasTimer = timerIds.containsKey(event.getPlayer());
		if (isPortal == hasTimer)
			return;

		final Player player = event.getPlayer();
		if (isPortal) {
			//player.sendMessage("scheduling task");
			final Block block = event.getTo().getBlock();

			int taskId = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				@Override
				public void run() {
					doPort(player, player, block);
				}
			}, 70);
			timerIds.put(player, taskId);
		}
		else {
			int taskId = timerIds.remove(player);
			plugin.getServer().getScheduler().cancelTask(taskId);
		}
	}

	Set<Player> portalStates = new HashSet<Player>();
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

	private void doPort(final Player player, final Entity entityToPort, final Block block) {
		Stack<Block> todo = new Stack<Block>();
		todo.push(block);

		Set<Block> portalsAndNeighboring = new HashSet<Block>();
		while (!todo.isEmpty()) {
			Block current = todo.pop();

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
				Block attached = current.getRelative(face);
				int typeId = attached.getTypeId();
				if (typeId != 63 && typeId != 68)
					continue;

				String[] lines = ((Sign)attached.getState()).getLines();

				if (!lines[0].equals("\u00a79[Portal]"))
					continue;

				final WarpDescriptor warpDescriptor;
				final String warpName = lines[1];
				if (lines[2].equalsIgnoreCase("private")) {
					try {
						warpDescriptor = plugin.warpEngine.getWarp(player, warpName);
					} catch (WarpException e) {
						player.sendMessage("\u00a7cYour entrance is blocked by a powerful entity.");
						return;
					}
				}
				else {
					warpDescriptor = plugin.warpEngine.getWarps().get(warpName.toLowerCase());
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
			}
		}
	}
}
