package de.doridian.yiffbukkit.listeners;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleListener;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.plugin.PluginManager;
import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.warp.WarpDescriptor;
import de.doridian.yiffbukkit.warp.WarpException;

public class SignPortalPlayerListener extends PlayerListener {
	final YiffBukkit plugin;

	public SignPortalPlayerListener(final YiffBukkit plugin) {
		this.plugin = plugin;

		plugin.playerHelper.registerMap(timerIds);


		PluginManager pm = plugin.getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_INTERACT, this, Priority.Monitor, plugin);
		pm.registerEvent(Event.Type.PLAYER_MOVE, this, Priority.Monitor, plugin);

		VehicleListener vehicleListener = new VehicleListener() {
			Set<Player> portalStates = new HashSet<Player>();

			{
				plugin.playerHelper.registerSet(portalStates);
			}
			@Override
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
		};
		pm.registerEvent(Event.Type.VEHICLE_UPDATE, vehicleListener, Priority.Highest, plugin);
	}

	private static final BlockFace[] faces = { BlockFace.NORTH,BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN };

	@Override
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
			final String playerName = player.getName();
			final WarpDescriptor warpDescriptor = plugin.warpEngine.getWarp(playerName, sign.getLine(1));
			sign.setLine(0, "§9[Portal]");
			sign.setLine(1, warpDescriptor.name);
			if (!plugin.permissionHandler.has(player, "yiffbukkit.signportal.public"))
				sign.setLine(2, "private");
			sign.setLine(3, playerName);
			sign.update(true);
		} catch (WarpException e) {
			plugin.playerHelper.sendDirectedMessage(player, e.getMessage(), e.getColor());
		}

		plugin.playerHelper.sendDirectedMessage(player, "Portal sign activated.");
	}

	Map<Player, Integer> timerIds = new HashMap<Player, Integer>();
	@Override
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

				if (!lines[0].equals("§9[Portal]"))
					continue;

				final WarpDescriptor warpDescriptor;
				final String warpName = lines[1];
				if (lines[2].equalsIgnoreCase("private")) {
					try {
						warpDescriptor = plugin.warpEngine.getWarp(player.getName(), warpName);
					} catch (WarpException e) {
						player.sendMessage("§cYour entrance is blocked by a powerful entity.");
						return;
					}
				}
				else {
					warpDescriptor = plugin.warpEngine.getWarps().get(warpName);
				}

				entityToPort.teleport(warpDescriptor.location);
				player.sendMessage("§9You're hurtled through the ethereal realm to your destination.");
			}
		}
	}
}
