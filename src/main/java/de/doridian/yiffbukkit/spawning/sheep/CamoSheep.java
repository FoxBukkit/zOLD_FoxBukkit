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
package de.doridian.yiffbukkit.spawning.sheep;

import de.doridian.yiffbukkit.core.YiffBukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Sheep;
import org.bukkit.material.Colorable;
import org.bukkit.material.MaterialData;
import org.bukkit.material.TexturedMaterial;

public class CamoSheep extends AbstractSheep {
	private DyeColor dyeMap[] = {
			null,
			DyeColor.SILVER,
			DyeColor.GREEN,
			DyeColor.BROWN,
			DyeColor.GRAY,
			DyeColor.BROWN,
			null, // sapling
			DyeColor.BLACK,
			DyeColor.BLUE,
			DyeColor.BLUE,
			DyeColor.RED,
			DyeColor.RED,
			DyeColor.YELLOW,
			DyeColor.PINK, // gravel
			DyeColor.YELLOW, // gold ore
			DyeColor.RED, // iron ore
			DyeColor.BLACK, // coal ore
			DyeColor.BROWN,
			DyeColor.GREEN,
			DyeColor.YELLOW,
			null, // glass
			DyeColor.BLUE,
			DyeColor.BLUE,
			DyeColor.GRAY,
			DyeColor.YELLOW,
			DyeColor.BROWN,
			DyeColor.RED,
			null, // unused
			null, // unused
			null, // unused
			null, // unused
			null, // unused
			null, // unused
			null, // unused
			null, // unused
			null, // wool
			null, // unused
			null, // yellow flower
			null, // red rose
			null, // brown mushroom
			null, // red mushroom
			DyeColor.YELLOW,
			DyeColor.WHITE,
			null, // double slab
			null, // slab
			DyeColor.RED,
			DyeColor.RED,
			DyeColor.BROWN,
			DyeColor.GREEN,
			DyeColor.BLACK,
			null, // torch
			DyeColor.RED,
			null, // mobspawner
			DyeColor.BROWN,
			DyeColor.BROWN,
			null, // wire
			DyeColor.CYAN,
			DyeColor.CYAN,
			DyeColor.BROWN,
			DyeColor.GREEN, // crops
			DyeColor.BROWN,
			DyeColor.GRAY,
			DyeColor.GRAY,
			null, // sign post
			null, // wood door
			null, // ladder
			null, // rails
			DyeColor.GRAY,
			null, // wall sign
			null, // lever
			DyeColor.SILVER, // stone plate
			null, // iron door
			DyeColor.BROWN,
			DyeColor.RED, // redstone ore
			DyeColor.RED, // glowing redstone ore
			null, // redstone torch
			null,
			null, // button
			DyeColor.WHITE, // snow cover
			DyeColor.LIGHT_BLUE,
			DyeColor.WHITE,
			DyeColor.GREEN,
			DyeColor.SILVER,
			null, // button
			DyeColor.BROWN,
			DyeColor.BROWN, // fence
			DyeColor.ORANGE,
			DyeColor.RED,
			DyeColor.BROWN,
			DyeColor.YELLOW,
			DyeColor.PURPLE, // portal
			DyeColor.ORANGE,
			DyeColor.WHITE,
			DyeColor.SILVER, // repeater
			DyeColor.SILVER, // repeater
	};

	public CamoSheep(YiffBukkit plugin, Sheep sheep) {
		super(plugin, sheep);
	}

	@Override
	public DyeColor getColor() {
		final Location location = sheep.getLocation();
		{
			final int blockId = location.getBlock().getTypeId();
			if (blockId < dyeMap.length && dyeMap[blockId] != null) {
				return dyeMap[blockId];
			}
		}

		location.setY(location.getY()-1);

		final Block block = location.getBlock();
		final MaterialData data = block.getState().getData();

		if (data instanceof Colorable) { // wool, etc.
			return ((Colorable) data).getColor();
		}

		if (data instanceof TexturedMaterial) { // steps, etc.
			return dyeMap[((TexturedMaterial) data).getMaterial().getId()];
		}

		final int blockId = block.getTypeId();
		if (blockId < dyeMap.length) {
			return dyeMap[blockId];
		}

		return null;
	}
}
