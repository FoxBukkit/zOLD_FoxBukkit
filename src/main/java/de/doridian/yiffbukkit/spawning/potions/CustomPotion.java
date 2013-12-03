package de.doridian.yiffbukkit.spawning.potions;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import net.minecraft.server.v1_7_R1.EntityPlayer;
import net.minecraft.server.v1_7_R1.EntityPotion;
import net.minecraft.server.v1_7_R1.ItemStack;
import net.minecraft.server.v1_7_R1.MovingObjectPosition;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;
import org.bukkit.entity.Entity;

public abstract class CustomPotion extends EntityPotion {
	protected final int potionId;
	protected final EntityPlayer thrower;

	public CustomPotion(Location location, int potionId, EntityPlayer thrower) {
		super(((CraftWorld) location.getWorld()).getHandle(), location.getX(), location.getY(), location.getZ(), new ItemStack(Material.POTION.getId(), 1, potionId));
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
			case ENTITY:
				if (hitEntity(movingobjectposition.entity.getBukkitEntity()) | hit(movingobjectposition))
					die();

				break;

			case TILE:
				final CraftWorld world = this.world.getWorld();
				Block block = world.getBlockAt(
						movingobjectposition.b, // v1_6_R2
						movingobjectposition.c, // v1_6_R2
						movingobjectposition.d // v1_6_R2
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
					throw new YiffBukkitCommandException("Invalid direction in TILE trace.");
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
				PlayerHelper.sendDirectedMessage((CommandSender) thrower.getBukkitEntity(), e.getMessage(), e.getColor());
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

	protected boolean hitEntity(Entity hitEntity) throws YiffBukkitCommandException {
		return false;
	}
}