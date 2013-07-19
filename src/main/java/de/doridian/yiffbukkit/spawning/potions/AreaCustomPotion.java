package de.doridian.yiffbukkit.spawning.potions;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import net.minecraft.server.v1_6_R2.EntityPlayer;
import net.minecraft.server.v1_6_R2.MovingObjectPosition;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public abstract class AreaCustomPotion extends CustomPotion {
	private double radius;

	public AreaCustomPotion(Location location, int potionId, EntityPlayer thrower, double radius) {
		super(location, potionId, thrower);
		this.radius = radius;
	}

	protected abstract void areaHit(Entity entity) throws YiffBukkitCommandException;
	protected void directHit(Entity entity) throws YiffBukkitCommandException {
		areaHit(entity);
	}

	@Override
	protected boolean hit(MovingObjectPosition movingobjectposition) throws YiffBukkitCommandException {
		final Entity thisBukkitEntity = getBukkitEntity();
		final World world = thisBukkitEntity.getWorld();
		world.playEffect(new Location(world, this.locX, this.locY, this.locZ), Effect.POTION_BREAK, potionId);

		Entity directHitEntity = null;
		if (movingobjectposition.entity != null) {
			directHitEntity = movingobjectposition.entity.getBukkitEntity();
			directHit(directHitEntity);
		}

		final Location thisLocation = thisBukkitEntity.getLocation();

		for (Entity entity: thisBukkitEntity.getNearbyEntities(radius, radius, radius)) {
			if (entity.getLocation().distanceSquared(thisLocation) > radius*radius)
				continue;

			if (entity.equals(thrower))
				continue;

			if (entity.equals(directHitEntity))
				continue;

			areaHit(entity);
		}

		return true;
	}
}