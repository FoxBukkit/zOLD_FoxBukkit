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
package de.doridian.foxbukkit.portal.listeners;

import de.doridian.foxbukkit.main.listeners.BaseListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class PortalPlayerListener extends BaseListener {
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
