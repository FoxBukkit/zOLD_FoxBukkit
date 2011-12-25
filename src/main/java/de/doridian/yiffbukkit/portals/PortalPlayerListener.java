package de.doridian.yiffbukkit.portals;

import de.doridian.yiffbukkit.YiffBukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class PortalPlayerListener extends PlayerListener {
	final YiffBukkit plugin;

	public PortalPlayerListener(YiffBukkit plugin) {
		this.plugin = plugin;

		PluginManager pm = plugin.getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_MOVE, this, Priority.Highest, plugin);
	}

	@Override
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
