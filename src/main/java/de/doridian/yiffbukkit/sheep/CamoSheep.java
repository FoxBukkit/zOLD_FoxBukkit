package de.doridian.yiffbukkit.sheep;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Sheep;

import de.doridian.yiffbukkit.YiffBukkit;

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
		Location location = sheep.getLocation();
		{
			int blockId = location.getBlock().getTypeId();
			if (blockId < dyeMap.length && dyeMap[blockId] != null) {
				return dyeMap[blockId];
			}
		}

		location.setY(location.getY()-1);

		{
			final Block block = location.getBlock();
			int blockId = block.getTypeId();
			if (blockId == 35) { // wool
				return DyeColor.getByData(block.getData());
			}
			if (blockId == 43 || blockId == 44) { // steps
				switch (block.getData()) {
				case 0:
					return DyeColor.SILVER;

				case 1:
					return DyeColor.YELLOW;

				case 2:
					return DyeColor.BROWN;

				case 3:
					return DyeColor.GRAY;
				}

				return DyeColor.getByData(block.getData());
			}
			else if (blockId < dyeMap.length) {
				return dyeMap[blockId];
			}
		}

		return null;
	}
}
