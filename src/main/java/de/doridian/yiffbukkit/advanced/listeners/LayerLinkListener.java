package de.doridian.yiffbukkit.advanced.listeners;

import de.doridian.yiffbukkit.advanced.LayerLinker;
import de.doridian.yiffbukkit.main.listeners.BaseListener;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;

public class LayerLinkListener extends BaseListener {
	private static final HashMap<Player, Long> teleportBlockedUntil = new HashMap<Player, Long>();

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerBlockPlace(BlockPlaceEvent event) {
		if(event.isCancelled())
			return;

		final Player player = event.getPlayer();
		if(!mayPlayerUse(player)) {
			event.setCancelled(true);
			return;
		}

		final Block block = event.getBlockPlaced();
		final Location location = block.getLocation();
		final LayerLinker.WorldAndY worldAndY = LayerLinker.getLinkedPoint(location.getWorld(), location.getBlockY());
		if(worldAndY == null)
			return;

		final Block otherWorldBlock = worldAndY.world.getBlockAt(location.getBlockX(), worldAndY.y, location.getBlockZ());
		otherWorldBlock.setTypeIdAndData(block.getTypeId(), block.getData(), true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerBlockBreak(BlockBreakEvent event) {
		if(event.isCancelled())
			return;

		final Player player = event.getPlayer();
		if(!mayPlayerUse(player)) {
			event.setCancelled(true);
			return;
		}

		final Block block = event.getBlock();
		final Location location = block.getLocation();
		final LayerLinker.WorldAndY worldAndY = LayerLinker.getLinkedPoint(location.getWorld(), location.getBlockY());
		if(worldAndY == null)
			return;

		final Block otherWorldBlock = worldAndY.world.getBlockAt(location.getBlockX(), worldAndY.y, location.getBlockZ());
		otherWorldBlock.setTypeIdAndData(0, (byte)0, true);
	}

	private boolean mayPlayerUse(Player player) {
		return player.hasPermission("yiffbukkit.world.layerlink");
	}

	private synchronized boolean isTeleportBlocked(Player player, long blockFor) {
		Long blockedUntil = teleportBlockedUntil.get(player);
		final long curTime = System.currentTimeMillis();
		if(blockedUntil != null && blockedUntil > curTime) {
			return true;
		}
		if(blockFor > 0) teleportBlockedUntil.put(player, curTime + blockFor);
		return false;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerTakeDamage(EntityDamageEvent event) {
		if(event.isCancelled())
			return;

		final Entity ent = event.getEntity();
		if(ent == null || !(ent instanceof Player))
			return;

		if(isTeleportBlocked((Player)ent, 0)) {
			final EntityDamageEvent.DamageCause cause = event.getCause();
			if(cause == EntityDamageEvent.DamageCause.VOID || cause == EntityDamageEvent.DamageCause.FALL) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerMove(PlayerMoveEvent event) {
		if(event.isCancelled())
			return;

		final Player player = event.getPlayer();
		if(!mayPlayerUse(player)) {
			event.setCancelled(true);
			return;
		}

		final Location location = event.getTo();

		if(event.getFrom().getY() < location.getY())
			return;

		int yPly = location.getBlockY();
		if(yPly > 200) {
			yPly--;
		}
		final LayerLinker.WorldAndY worldAndY = LayerLinker.getLinkedPoint(location.getWorld(), yPly);
		if(worldAndY == null)
			return;

		if(isTeleportBlocked(player, 5000)) {
			event.setTo(event.getFrom());
			return;
		}

		final Location otherLocation = location.clone();
		otherLocation.setWorld(worldAndY.world);
		otherLocation.setY(worldAndY.y + 1);
		event.setCancelled(true);

		player.teleport(otherLocation);
	}
}
