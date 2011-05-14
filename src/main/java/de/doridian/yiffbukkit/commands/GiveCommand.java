package de.doridian.yiffbukkit.commands;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("give")
@Help("Gives resource (use _ for spaces in name!)")
@Usage("<name or id> [amount] [player]")
@Level(100) // See CanPlayerUseCommand
public class GiveCommand extends ICommand {
	private static final Map<String,Material> aliases = new HashMap<String,Material>();
	private static final Map<String,Short> dataValues = new HashMap<String, Short>();
	static {
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
	@Override
	public boolean CanPlayerUseCommand(Player ply)
	{
		int plylvl = plugin.playerHelper.GetPlayerLevel(ply);
		int reqlvl = (ply.getWorld().getName().substring(0, 2).toLowerCase() == "rp_") ? 100 : 3;

		return (plylvl >= reqlvl);
	}

	static Material matchMaterial(String materialName) {
		Material material = aliases.get(materialName);
		if (material != null)
			return material;

		return Material.matchMaterial(materialName);
	}

	@Override
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

		Player target = otherName == null ? ply : playerHelper.MatchPlayerSingle(otherName);


		String materialName = args[0];
		int colonPos = materialName.indexOf(':');
		String colorName = null;
		if (colonPos >= 0) {
			colorName = materialName.substring(colonPos+1);
			materialName = materialName.substring(0, colonPos);
		}
		Material material = matchMaterial(materialName);
		if (material == null) {
			if (playerHelper.GetPlayerLevel(ply) < 4)
				throw new YiffBukkitCommandException("Material "+materialName+" not found");

			if (count > 10)
				count = 10;

			for (int i = 0; i < count; ++i) {
				try {
					plugin.utils.buildMob(args[0].toUpperCase().split("\\+"), ply, target, target.getLocation());
				}
				catch (YiffBukkitCommandException e) {
					playerHelper.SendDirectedMessage(ply, "Material "+materialName+" not found");
					throw e;
				}
			}

			playerHelper.SendDirectedMessage(ply, "Created "+count+" creatures.");
			return;
		}


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

		PlayerInventory inv = target.getInventory();
		int empty = inv.firstEmpty();
		inv.setItem(empty, stack);

		if (target == ply)
			playerHelper.SendDirectedMessage(ply, "Item has been put in first free slot of your inventory!");
		else
			playerHelper.SendDirectedMessage(ply, "Item has been put in first free slot of "+target.getName()+"'s inventory!");
	}
}
