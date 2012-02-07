package de.doridian.yiffbukkit.warp.portals;

import de.doridian.yiffbukkitsplit.YiffBukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class PortalPlayerListener implements Listener {
	final YiffBukkit plugin;

	public PortalPlayerListener(YiffBukkit plugin) {
		this.plugin = plugin;

		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerMove(PlayerMoveEvent event) {
		Location from = event.getFrom();
		Location to = event.getTo();
		final Vector fromVector = from.toVector();
		Vector direction = to.toVector().subtract(fromVector);
		int length = (int)Math.ceil(direction.length());
		direction.normalize();

		for (BlockIterator blockIterator = new BlockIterator(from.getWorld(), fromVector, direction, 0, length); blockIterator.hasNext();) {
			Block block = blockIterator.next();

			if (block.getType() != Material.PORTAL)
				continue;

			plugin.portalEngine.handlePortal(event);
			break;
		}

		/*if (event.getFrom().getBlock().getType() != Material.PORTAL)
			return;

		plugin.portalEngine.handlePortal(event);*/
	}
}
