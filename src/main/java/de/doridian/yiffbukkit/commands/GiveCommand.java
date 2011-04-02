package de.doridian.yiffbukkit.commands;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.YiffBukkitCommandException;

public class GiveCommand extends ICommand {
	Map<String,Material> aliases = new HashMap<String,Material>();
	Map<String,Short> dataValues = new HashMap<String, Short>();
	{
		aliases.put("wood_shovel", Material.WOOD_SPADE);
		aliases.put("wooden_spade", Material.WOOD_SPADE);
		aliases.put("wooden_shovel", Material.WOOD_SPADE);
		aliases.put("gold_shovel", Material.GOLD_SPADE);
		aliases.put("golden_spade", Material.GOLD_SPADE);
		aliases.put("golden_shovel", Material.GOLD_SPADE);
		aliases.put("golden_pickaxe", Material.GOLD_PICKAXE);
		aliases.put("golden_sword", Material.GOLD_SWORD);
		aliases.put("golden_hoe", Material.GOLD_HOE);
		aliases.put("golden_axe", Material.GOLD_AXE);
		aliases.put("golden_helmet", Material.GOLD_HELMET);
		aliases.put("golden_chestplate", Material.GOLD_CHESTPLATE);
		aliases.put("golden_leggings", Material.GOLD_LEGGINGS);
		aliases.put("golden_boots", Material.GOLD_BOOTS);
		aliases.put("stone_shovel", Material.STONE_SPADE);
		aliases.put("iron_shovel", Material.IRON_SPADE);
		aliases.put("diamond_shovel", Material.DIAMOND_SPADE);

		aliases.put("leaf", Material.LEAVES);
		aliases.put("noteblock", Material.NOTE_BLOCK);
		aliases.put("cloth", Material.WOOL);
		aliases.put("slab", Material.STEP);
		aliases.put("stone_slab", Material.STEP);
		aliases.put("stoneslab", Material.STEP);
		aliases.put("shelf", Material.BOOKSHELF);
		aliases.put("mossy_cobble", Material.MOSSY_COBBLESTONE);
		aliases.put("mobspawner", Material.MOB_SPAWNER);
		aliases.put("wooden_stairs", Material.WOOD_STAIRS);
		aliases.put("cobble_stairs", Material.COBBLESTONE_STAIRS);
		aliases.put("redstone_torch", Material.REDSTONE_TORCH_ON);
		aliases.put("diode_block", Material.DIODE_BLOCK_OFF);
		aliases.put("gunpowder", Material.SULPHUR);
		aliases.put("fish", Material.RAW_FISH);
		aliases.put("button", Material.STONE_BUTTON);
		aliases.put("bukkit", Material.BUCKET);
		aliases.put("water_bukkit", Material.WATER_BUCKET);
		aliases.put("lava_bukkit", Material.LAVA_BUCKET);
		aliases.put("milk_bukkit", Material.MILK_BUCKET);
		aliases.put("yiff_bukkit", Material.MILK_BUCKET);
		aliases.put("yiffbukkit", Material.MILK_BUCKET);
		aliases.put("dye", Material.INK_SACK);
		aliases.put("ink", Material.INK_SACK);
		aliases.put("repeater", Material.DIODE);

		dataValues.put("43:SANDSTONE", (short) 1);
		dataValues.put("43:WOOD", (short) 2);
		dataValues.put("43:COBBLE", (short) 3);
		dataValues.put("43:COBBLESTONE", (short) 3);

		dataValues.put("44:SANDSTONE", (short) 1);
		dataValues.put("44:WOOD", (short) 2);
		dataValues.put("44:COBBLE", (short) 3);
		dataValues.put("44:COBBLESTONE", (short) 3);

		dataValues.put("17:REDWOOD", (short) 1);
		dataValues.put("17:DARK", (short) 1);
		dataValues.put("17:BIRCH", (short) 2);
		dataValues.put("17:LIGHT", (short) 2);
	};

	public int GetMinLevel() {
		return 3;
	}

	public GiveCommand(YiffBukkit plug) {
		super(plug);
	}

	private Material matchMaterial(String materialName) {
		Material material = aliases.get(materialName);
		if (material != null)
			return material;

		return Material.matchMaterial(materialName);
	}

	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		Integer count = 1;
		String otherName = null;
		try {
			count = Integer.valueOf(args[1]);
			if (args.length >= 3)
				otherName = args[2];
		}
		catch(Exception e) {
			if (args.length >= 2)
				otherName = args[1];
		}
		String materialName = args[0];
		int colonPos = materialName.indexOf(':');
		String colorName = null;
		if (colonPos >= 0) {
			colorName = materialName.substring(colonPos+1);
			materialName = materialName.substring(0, colonPos);
		}
		Material material = matchMaterial(materialName);
		if (material == null)
			throw new YiffBukkitCommandException("Material "+materialName+" not found");

		ItemStack stack = new ItemStack(material, count);

		if (colorName != null) {
			colorName = colorName.toUpperCase();
			Short dataValue = dataValues.get(material.getId()+":"+colorName);
			if (dataValue != null) {
				stack.setDurability(dataValue);
			}
			else if (material == Material.WOOL || material == Material.INK_SACK) {
				try {
					DyeColor dyeColor = DyeColor.valueOf(colorName.replace("GREY", "GRAY"));

					if (material == Material.WOOL)
						stack.setDurability(dyeColor.getData());
					else
						stack.setDurability((short) (15-dyeColor.getData()));
				}
				catch (IllegalArgumentException e) {
					throw new YiffBukkitCommandException("Color "+colorName+" not found", e);
				}
			}
			else {
				throw new YiffBukkitCommandException("Material "+materialName+" cannot have a data value.");
			}
		}

		if (otherName == null) {
			PlayerInventory inv = ply.getInventory();
			int empty = inv.firstEmpty();
			inv.setItem(empty, stack);
			playerHelper.SendDirectedMessage(ply, "Item has been put in first free slot of your inventory!");
		}
		else {
			Player otherply = playerHelper.MatchPlayerSingle(otherName);

			PlayerInventory inv = otherply.getInventory();
			int empty = inv.firstEmpty();
			inv.setItem(empty, stack);
			playerHelper.SendDirectedMessage(ply, "Item has been put in first free slot of "+otherply.getName()+"'s inventory!");
		}
	}

	public String GetHelp() {
		return "Gives resource (use _ for spaces in name!)";
	}

	public String GetUsage() {
		return "<name or id> [amount] [player]";
	}
}
