package de.doridian.yiffbukkit.spawning.potions;

import de.doridian.yiffbukkit.core.util.PlayerHelper;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.util.Utils;
import net.minecraft.server.v1_7_R1.EntityPlayer;
import net.minecraft.server.v1_7_R1.EntityPotion;
import net.minecraft.server.v1_7_R1.ItemStack;
import net.minecraft.server.v1_7_R1.MovingObjectPosition;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;
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
						movingobjectposition.b, // v1_7_R1
						movingobjectposition.c, // v1_7_R1
						movingobjectposition.d // v1_7_R1
				);
				Location hitVec = Utils.toLocation(movingobjectposition.pos, world);

				final BlockFace sideHit;
				switch (movingobjectposition.face) {
				case 0:
					sideHit = BlockFace.DOWN;
					break;

				case 1:
					sideHit = BlockFace.UP;
					break;

				case 2:
					sideHit = BlockFace.EAST;
					break;

				case 3:
					sideHit = BlockFace.WEST;
					break;

				case 4:
					sideHit = BlockFace.NORTH;
					break;

				case 5:
					sideHit = BlockFace.SOUTH;
					break;
				default:
					throw new YiffBukkitCommandException("Invalid direction in BLOCK trace.");
				}
				if (hitBlock(block, sideHit, hitVec) | hit(movingobjectposition))
					die();

				break;
			}
		}
		catch (YiffBukkitCommandException e) {
			if (thrower == null) {
				System.out.println("\u00a7"+e.getColor()+"[YB]\u00a7f " + e.getMessage());
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

	protected boolean hit(MovingObjectPosition movingobjectposition) throws YiffBukkitCommandException {
		return false;
	}

	protected boolean hitBlock(Block hitBlock, BlockFace sideHit, Location hitLocation) throws YiffBukkitCommandException {
		return false;
	}

	protected boolean hitEntity(@SuppressWarnings("UnusedParameters") Entity hitEntity) throws YiffBukkitCommandException {
		return false;
	}
}
