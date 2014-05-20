/**
 * This file is part of YiffBukkit.
 *
 * YiffBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * YiffBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with YiffBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.doridian.yiffbukkit.fun.listeners;

import de.doridian.yiffbukkit.main.listeners.BaseListener;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Jukebox;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockRedstoneEvent;

public class JukeboxListener extends BaseListener {
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockRedstoneChange(BlockRedstoneEvent event) {
		final Block src = event.getBlock();

		checkJukebox(src.getRelative(0, 0, 1));
		checkJukebox(src.getRelative(0, 0, -1));
		checkJukebox(src.getRelative(1, 0, 0));
		checkJukebox(src.getRelative(-1, 0, 0));
		checkJukebox(src.getRelative(0, -1, 0));
		checkJukebox(src.getRelative(0, 1, 0));
	}

	private void checkJukebox(Block block) {
		if (block.getType() != Material.JUKEBOX)
			return;

		final BlockState blockState = block.getState();
		if (!(blockState instanceof Jukebox))
			return;

		final Jukebox jb = (Jukebox) blockState;

		final Material record;
		if (block.isBlockPowered()) {
			record = jb.getPlaying();
		}
		else {
			record = Material.AIR;
		}

		setPlaying(block, record);
	}

	private static void setPlaying(Block block, Material record) {
		block.getWorld().playEffect(block.getLocation(), Effect.RECORD_PLAY, record.getId());
	}
}
