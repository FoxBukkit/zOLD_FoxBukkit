package de.doridian.yiffbukkit.spawning.potions;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import net.minecraft.server.v1_5_R1.EntityPlayer;
import net.minecraft.server.v1_5_R1.EntityPotion;
import net.minecraft.server.v1_5_R1.ItemStack;
import net.minecraft.server.v1_5_R1.MovingObjectPosition;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_5_R1.CraftWorld;

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
		if (movingobjectposition.entity == thrower)
			return;

		try {
			if (hit(movingobjectposition))
				die();
		}
		catch (YiffBukkitCommandException e) {
			PlayerHelper.sendDirectedMessage((CommandSender) thrower.getBukkitEntity(), e.getMessage(), e.getColor());
			die();
		}
		catch (Throwable e) {
			e.printStackTrace();
			die();
		}
	}

	protected abstract boolean hit(MovingObjectPosition movingobjectposition) throws YiffBukkitCommandException;
}