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
package com.foxelbox.foxbukkit.spawning.potions;

import com.foxelbox.foxbukkit.core.util.PlayerHelper;
import com.foxelbox.foxbukkit.main.FoxBukkitCommandException;
import com.foxelbox.foxbukkit.main.util.Utils;
import net.minecraft.server.v1_8_R1.EntityPlayer;
import net.minecraft.server.v1_8_R1.EntityPotion;
import net.minecraft.server.v1_8_R1.ItemStack;
import net.minecraft.server.v1_8_R1.MovingObjectPosition;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.entity.Entity;

public abstract class CustomPotion extends EntityPotion {
	protected final int potionId;
	protected final EntityPlayer thrower;

	public CustomPotion(Location location, int potionId, EntityPlayer thrower) {
		super(((CraftWorld) location.getWorld()).getHandle(), location.getX(), location.getY(), location.getZ(), new ItemStack(Utils.getItemByMaterial(Material.POTION), 1, potionId));
		this.potionId = potionId;
		this.thrower = thrower;
	}

	@Override
	protected void a(MovingObjectPosition movingobjectposition) {
		if (this.dead)
			return;

		if (thrower != null && movingobjectposition.entity == thrower)
			return;

		try {
			switch (movingobjectposition.type) {
			case MISS:
				break;

			case ENTITY:
				if (hitEntity(movingobjectposition.entity.getBukkitEntity()) | hit(movingobjectposition))
					die();

				break;

			case BLOCK:
				final CraftWorld world = this.world.getWorld();
				Block block = world.getBlockAt(
						movingobjectposition.e.getX(), // v1_7_R1
						movingobjectposition.e.getY(), // v1_7_R1
						movingobjectposition.e.getZ() // v1_7_R1
				);
				Location hitVec = Utils.toLocation(movingobjectposition.pos, world);

				final BlockFace sideHit;
				switch (movingobjectposition.direction) {
				case DOWN:
					sideHit = BlockFace.DOWN;
					break;

				case UP:
					sideHit = BlockFace.UP;
					break;

				case EAST:
					sideHit = BlockFace.EAST;
					break;

				case WEST:
					sideHit = BlockFace.WEST;
					break;

				case NORTH:
					sideHit = BlockFace.NORTH;
					break;

				case SOUTH:
					sideHit = BlockFace.SOUTH;
					break;
				default:
					throw new FoxBukkitCommandException("Invalid direction in BLOCK trace.");
				}
				if (hitBlock(block, sideHit, hitVec) | hit(movingobjectposition))
					die();

				break;
			}
		}
		catch (FoxBukkitCommandException e) {
			if (thrower == null) {
				System.out.println("\u00a7"+e.getColor()+"[FB]\u00a7f " + e.getMessage());
			}
			else {
				PlayerHelper.sendDirectedMessage(thrower.getBukkitEntity(), e.getMessage(), e.getColor());
			}
			die();
		}
		catch (Throwable e) {
			e.printStackTrace();
			die();
		}
	}

	protected boolean hit(MovingObjectPosition movingobjectposition) throws FoxBukkitCommandException {
		return false;
	}

	protected boolean hitBlock(Block hitBlock, BlockFace sideHit, Location hitLocation) {
		return false;
	}

	protected boolean hitEntity(@SuppressWarnings("UnusedParameters") Entity hitEntity) {
		return false;
	}
}
