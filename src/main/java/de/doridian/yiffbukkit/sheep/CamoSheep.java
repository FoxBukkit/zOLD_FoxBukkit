package de.doridian.yiffbukkit.sheep;

import org.bukkit.DyeColor;
import org.bukkit.Location;
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
			DyeColor.YELLOW,
			DyeColor.RED,
			DyeColor.BLACK,
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
			null,
			null,
			null,
			null,
			DyeColor.YELLOW,
			DyeColor.WHITE,
			DyeColor.SILVER, // todo: evaluate type
			DyeColor.SILVER, // todo: evaluate type
			DyeColor.RED,
			DyeColor.RED,
			DyeColor.BROWN,
			DyeColor.GREEN,
			DyeColor.BLACK,
			null, // torch
			null,
			null,
			DyeColor.BROWN,
			DyeColor.BROWN,
			null, // wire
			DyeColor.CYAN,
			DyeColor.CYAN,
			DyeColor.BROWN,
			null, // crops
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
			null, // stone plate
			null, // iron door
			null, // wood plate
			DyeColor.RED,
			DyeColor.RED,
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
			null, // portal
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
			int blockId = location.getBlock().getTypeId();
			if (blockId < dyeMap.length) {
				return dyeMap[blockId];
			}
		}

		return null;
	}
}
