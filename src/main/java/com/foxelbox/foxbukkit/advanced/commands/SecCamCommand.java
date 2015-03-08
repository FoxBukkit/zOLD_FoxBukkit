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
package com.foxelbox.foxbukkit.advanced.commands;

import com.sk89q.worldedit.blocks.BlockType;
import com.foxelbox.foxbukkit.core.util.MessageHelper;
import com.foxelbox.foxbukkit.main.FoxBukkitCommandException;
import com.foxelbox.foxbukkit.main.commands.system.ICommand;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Names;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Permission;
import net.minecraft.server.v1_8_R2.MaterialMapColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R2.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

@Names("seccam")
@Permission("worldedit.generation.map")
public class SecCamCommand extends ICommand {
	@Override
	public void Run(final Player ply, String[] args, String argStr, String commandName) throws FoxBukkitCommandException {
		final ItemStack itemInHand = ply.getItemInHand();
		final Material itemInHandType = itemInHand.getType();
		if (itemInHandType != Material.MAP)
			throw new FoxBukkitCommandException("Not a map!");

		final MapView mapView = Bukkit.getMap(itemInHand.getDurability());
		for (MapRenderer mapRenderer : mapView.getRenderers()) {
			mapView.removeRenderer(mapRenderer);
		}

		final double zoom = 1;
		final MapRenderer mapRenderer = new MapRenderer() {
			boolean die = false;
			double t = 0.0;

			@Override
			public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
				if (die)
					return;

				die = true;
				t += 1.0/20.0;
				// cam coords
				final Location location = ply.getEyeLocation();
				final Vector origin = location.toVector();
				double rx = -Math.toRadians(location.getPitch());
				double ry = -Math.toRadians(location.getYaw());
				double rz = 0;
				double sx = Math.sin(rx);
				double cx = Math.cos(rx);
				double sy = Math.sin(ry);
				double cy = Math.cos(ry);
				double sz = Math.sin(rz);
				double cz = Math.cos(rz);

				for (int xp2 = 0; xp2 < 128; ++xp2) {
					for (int yp2 = 0; yp2 < 128; ++yp2) {
						double x = xp2 / 64.0 - 1.0;
						double y = yp2 / 64.0 - 1.0;
						final byte color = genPixel(origin, sx, cx, sy, cy, sz, cz, x, y);

						mapCanvas.setPixel(xp2, yp2, color);
					}
				}
			}

			private byte genPixel(Vector origin, double sx, double cx, double sy, double cy, double sz, double cz, double x, double y) {
				// proj. plane coords
				double dx = x / zoom;
				double dy = y / zoom;
				double dz = 1;

				// z rotation
				{
					final double dx1 = dx * cz - dy * sz;
					dy = dx * sz + dy * cz;
					dx = dx1;
				}

				// x rotation
				{
					final double dy1 = dy * cx - dz * sx;
					dz = dy * sx + dz * cx;
					dy = dy1;
				}

				// y rotation
				{
					final double dx1 = dx * cy - dz * sy;
					final double dz1 = dx * sy + dz * cy;
					dx = dx1; dz = dz1;
				}

				final BlockIterator blockIterator = new BlockIterator(ply.getWorld(), origin, new Vector(-dx, -dy, dz), 0, 300);
				while (blockIterator.hasNext()) {
					final Block next = blockIterator.next();

					final int nextY = next.getY();
					if (nextY == 0)
						return 0;

					if ((nextY & 0xFF) != nextY)
						return 0;

					Material type = next.getType();
					switch (type) {
					case WATER:
					case STATIONARY_WATER:
					case LAVA:
					case STATIONARY_LAVA:
						break;
					default:
						if (BlockType.canPassThrough(next.getTypeId(), next.getData()))
							continue;
					}

					if (next.getRelative(0, 1, 0).getType() == Material.SNOW)
						type = Material.SNOW;
					final net.minecraft.server.v1_8_R2.Block notchBlock = CraftMagicNumbers.getBlock(type);
					//final MaterialMapColor materialMapColor = notchBlock.g(next.);
					int offset = 0;
					return 0;
					//return (byte) (materialMapColor.M * 4 + offset);
				}
				return 0;
			}
		};
		mapView.addRenderer(mapRenderer);

		MessageHelper.sendMessage(ply, "" + mapView.getId());

		ply.sendMap(mapView);
	}
}
