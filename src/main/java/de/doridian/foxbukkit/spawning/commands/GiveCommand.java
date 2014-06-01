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
package de.doridian.foxbukkit.spawning.commands;

import de.doridian.foxbukkit.core.util.PlayerHelper;
import de.doridian.foxbukkit.main.FoxBukkitCommandException;
import de.doridian.foxbukkit.main.PermissionDeniedException;
import de.doridian.foxbukkit.main.commands.system.ICommand;
import de.doridian.foxbukkit.main.commands.system.ICommand.Help;
import de.doridian.foxbukkit.main.commands.system.ICommand.Level;
import de.doridian.foxbukkit.main.commands.system.ICommand.Names;
import de.doridian.foxbukkit.main.commands.system.ICommand.Usage;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Colorable;
import org.bukkit.material.MaterialData;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@Names({"give", "item", "i"})
@Help("Gives resource (use _ for spaces in name!)")
@Usage("<name or id> [amount] [player]")
@Level(1)
public class GiveCommand extends ICommand {
	private static final Map<String, Material> aliases = new HashMap<>();
	private static final Map<String, Short> dataValues = new HashMap<>();
	private static final Map<Material, Double> prices = new EnumMap<>(Material.class);

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

		aliases.put("dye", Material.INK_SACK);
		aliases.put("ink", Material.INK_SACK);
		aliases.put("repeater", Material.DIODE);
		aliases.put("piston", Material.PISTON_BASE);
		aliases.put("sticky_piston", Material.PISTON_STICKY_BASE);
		aliases.put("piston_sticky", Material.PISTON_STICKY_BASE);
		aliases.put("reed", Material.SUGAR_CANE);

		dataValues.put("43:SANDSTONE", (short) 1);
		dataValues.put("43:WOOD", (short) 2);
		dataValues.put("43:COBBLE", (short) 3);
		dataValues.put("43:COBBLESTONE", (short) 3);
		dataValues.put("43:BRICK", (short) 4);
		dataValues.put("43:STONEBRICK", (short) 5);

		dataValues.put("44:SANDSTONE", (short) 1);
		dataValues.put("44:WOOD", (short) 2);
		dataValues.put("44:COBBLE", (short) 3);
		dataValues.put("44:COBBLESTONE", (short) 3);
		dataValues.put("44:BRICK", (short) 4);
		dataValues.put("44:STONEBRICK", (short) 5);

		for (short i = 1; i <= 5; ++i) {
			dataValues.put("43:" + i, i);
			dataValues.put("44:" + i, i);
		}

		dataValues.put("5:REDWOOD", (short) 1);
		dataValues.put("5:DARK", (short) 1);
		dataValues.put("5:PINE", (short) 1);
		dataValues.put("5:SPRUCE", (short) 1);
		dataValues.put("5:BIRCH", (short) 2);
		dataValues.put("5:LIGHT", (short) 2);
		dataValues.put("5:JUNGLE", (short) 3);
		dataValues.put("5:TROPIC", (short) 3);

		dataValues.put("17:REDWOOD", (short) 1);
		dataValues.put("17:DARK", (short) 1);
		dataValues.put("17:PINE", (short) 1);
		dataValues.put("17:SPRUCE", (short) 1);
		dataValues.put("17:BIRCH", (short) 2);
		dataValues.put("17:LIGHT", (short) 2);
		dataValues.put("17:JUNGLE", (short) 3);
		dataValues.put("17:TROPIC", (short) 3);

		for (short i = 1; i <= 3; ++i) {
			dataValues.put("5:" + i, i);
			dataValues.put("17:" + i, i);
		}

		prices.put(Material.BEDROCK, 1000.0);

		prices.put(Material.PORTAL, 100000000.0);
		prices.put(Material.ENDER_PORTAL, 100000000.0);
		prices.put(Material.ENDER_PORTAL_FRAME, 100000000.0);

		prices.put(Material.DIAMOND_AXE, 150.0);
		prices.put(Material.DIAMOND_HOE, 100.0);
		prices.put(Material.DIAMOND_PICKAXE, 150.0);
		prices.put(Material.DIAMOND_SPADE, 50.0);
		prices.put(Material.DIAMOND_SWORD, 100.0);

		prices.put(Material.SLIME_BALL, 1.0);
	}

	private static final double DEFAULT_PRICE = 0.0;

	public static Material matchMaterial(String materialName) {
		Material material = aliases.get(materialName.toLowerCase());
		if (material != null)
			return material;

		return Material.matchMaterial(materialName);
	}

	public static Short getDataValue(final Material material, String dataName) {
		return dataValues.get(material.getId() + ":" + dataName);
	}

	public static double getPrice(final Material material) {
		final Double price = prices.get(material);
		if (price == null)
			return DEFAULT_PRICE;

		return price;
	}

	@Override
	public void run(CommandSender commandSender, String[] args, String argStr, String commandName) throws FoxBukkitCommandException {
		Integer count;
		String otherName;
		try {
			switch (args.length) {
			case 0:
				throw new FoxBukkitCommandException("Not enough arguments");

			case 1:
				// <name or id>
				count = 1;
				otherName = null;
				break;

			case 2:
				// <name or id> <amount>
				count = Integer.valueOf(args[1]);
				otherName = null;
				break;

			default:
				// <name or id> <amount> <player>
				count = Integer.valueOf(args[1]);
				otherName = args[2];
				break;
			}
		}
		catch (NumberFormatException e) {
			// <name or id> <player>
			count = 1;
			otherName = args[1];
		}

		if(count < 1)
			throw new FoxBukkitCommandException("Invalid count (less than 1)");

		final Player target;
		final Location targetLocation;
		if (otherName != null) {
			target = playerHelper.matchPlayerSingle(otherName);
			targetLocation = target.getLocation();
			targetLocation.setY(targetLocation.getY() - 1.62);
		}
		else {
			targetLocation = getCommandSenderLocation(commandSender, true);
			if (commandSender instanceof Player) {
				targetLocation.setY(targetLocation.getY() - 1.62);
				target = (Player) commandSender;
			}
			else {
				target = null;
			}
		}

		String materialName = args[0];
		final int colonPos = materialName.indexOf(':');
		String colorName = null;
		if (colonPos >= 0) {
			colorName = materialName.substring(colonPos + 1);
			materialName = materialName.substring(0, colonPos);
		}
		final Material material = matchMaterial(materialName);
		if (material == null || target == null) {
			if (count > 10)
				count = 10;

			for (int i = 0; i < count; ++i) {
				try {
					plugin.spawnUtils.buildMob(args[0].split("\\+"), commandSender, target, targetLocation);
				}
				catch (PermissionDeniedException e) {
					throw new FoxBukkitCommandException("Material " + materialName + " not found");
				}
				catch (FoxBukkitCommandException e) {
					PlayerHelper.sendDirectedMessage(commandSender, "Material " + materialName + " not found");
					throw e;
				}
			}

			PlayerHelper.sendDirectedMessage(commandSender, "Created " + count + " creatures.");
			return;
		}

		if (material == Material.AIR)
			throw new FoxBukkitCommandException("Material " + materialName + " not found");

		final ItemStack stack = new ItemStack(material, count);

		if (colorName != null) {
			colorName = colorName.toUpperCase();
			final Short dataValue = getDataValue(material, colorName);
			if (dataValue != null) {
				stack.setDurability(dataValue);
			}
			else {
				final MaterialData data = stack.getData();
				if (data instanceof Colorable) {
					try {
						final DyeColor dyeColor = DyeColor.valueOf(colorName.toUpperCase().replace("GREY", "GRAY"));

						final Colorable colorable = (Colorable) data;
						colorable.setColor(dyeColor);
						stack.setData((MaterialData) colorable);
					}
					catch (IllegalArgumentException e) {
						throw new FoxBukkitCommandException("Color " + colorName + " not found", e);
					}
				}
				else {
					stack.setDurability(Short.parseShort(colorName));
				}
			}
		}

		final double price = getPrice(material) * count;
		final boolean usedFunds = plugin.bank.checkPermissionsOrUseFunds(commandSender, "foxbukkit.players.give", price, "/give " + argStr);

		if (usedFunds) {
			final double total = plugin.bank.getBalance(commandSender.getUniqueId());
			PlayerHelper.sendDirectedMessage(commandSender, "Used " + price + " YP from your account. You have " + total + " YP left.");
		}

		target.getInventory().addItem(stack);

		if (target == commandSender) {
			PlayerHelper.sendDirectedMessage(commandSender, "Item has been put in first free slot of your inventory!");
		}
		else {
			PlayerHelper.sendDirectedMessage(commandSender, "Item has been put in first free slot of " + target.getName() + "'s inventory!");
		}
	}
}
