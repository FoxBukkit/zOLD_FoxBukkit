package de.doridian.yiffbukkit.transmute;


import net.minecraft.server.MathHelper;
import net.minecraft.server.Packet21PickupSpawn;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.server.Packet;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.GiveCommand;

public class ItemShape extends EntityShape {
	private int type = 81; // cactus
	private int data = 0;
	private int count = 1;

	public ItemShape(Transmute transmute, Player player, Entity entity, int mobType) {
		super(transmute, player, entity, mobType);

		//yOffset = 1.62;
		dropping = true;
	}

	@Override
	protected Packet createSpawnPacket() {
		Location location = entity.getLocation();

		final Packet21PickupSpawn p21 = new Packet21PickupSpawn();

		p21.a = entityId;

		p21.b = MathHelper.floor(location.getX() * 32.0D);
		p21.c = MathHelper.floor((location.getY()+yOffset) * 32.0D);
		p21.d = MathHelper.floor(location.getZ() * 32.0D);

		Vector velocity = entity.getVelocity();
		p21.e = (byte) ((int) (velocity.getX() * 128.0D));
		p21.f = (byte) ((int) (velocity.getY() * 128.0D));
		p21.g = (byte) ((int) (velocity.getZ() * 128.0D));

		p21.h = type;
		p21.i = count;
		p21.m = data;

		return p21;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;

		deleteEntity();
		createTransmutedEntity();
	}

	public int getData() {
		return data;
	}

	public void setData(int data) {
		this.data = data;

		deleteEntity();
		createTransmutedEntity();
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;

		deleteEntity();
		createTransmutedEntity();
	}

	public void setType(int type, int data) {
		this.type = type;
		this.data = data;

		deleteEntity();
		createTransmutedEntity();
	}

	public void setType(int type, int data, int count) {
		this.type = type;
		this.data = data;
		this.count = count;

		deleteEntity();
		createTransmutedEntity();
	}

	@Override
	protected void runAction(String actionName, String[] args, String argStr) throws YiffBukkitCommandException {
		if (actionName.equalsIgnoreCase("type")) {
			final int count;
			if (args.length >= 2) {
				try {
					count = Integer.valueOf(args[1]);
				}
				catch(NumberFormatException e) {
					throw new YiffBukkitCommandException("Number expected");
				}
			}
			else {
				count = this.count;
			}

			String materialName = args[0];
			final int colonPos = materialName.indexOf(':');
			String colorName = null;
			if (colonPos >= 0) {
				colorName = materialName.substring(colonPos+1);
				materialName = materialName.substring(0, colonPos);
			}
			final Material material = de.doridian.yiffbukkit.commands.GiveCommand.matchMaterial(materialName);
			if (material == null) {
				throw new YiffBukkitCommandException("Material "+materialName+" not found");
			}

			if (material.getId() == 0)
				throw new YiffBukkitCommandException("Material "+materialName+" not found");

			final ItemStack stack = new ItemStack(material, count, (short) data);

			if (colorName != null) {
				colorName = colorName.toUpperCase();
				Short dataValue = GiveCommand.getDataValue(material, colorName);
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

			setType(stack.getTypeId(), stack.getDurability(), stack.getAmount());
		}
	}
}
