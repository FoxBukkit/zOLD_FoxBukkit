/**
 * This file is part of YiffBukkit.
 *
 * YiffBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * YiffBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with YiffBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.doridian.yiffbukkit.spawning.potions;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.MovingObjectPosition;
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

	protected abstract void areaHit(Entity entity);
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