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
package com.foxelbox.foxbukkit.spawning;

import com.foxelbox.foxbukkit.core.FoxBukkit;
import com.foxelbox.foxbukkit.main.util.Utils;
import com.foxelbox.foxbukkit.spawning.effects.system.FBEffect;
import com.foxelbox.foxbukkit.spawning.potions.CustomPotion;
import de.diddiz.LogBlock.Actor;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.EntityFallingBlock;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutExplosion;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class Meteor extends CustomPotion {
	private final double radius;
	private final double speed;

	public Meteor(Location location, EntityPlayer thrower, double radius, double speed) {
		super(location, 8, thrower); // TODO: pick different potionId
		this.radius = radius;
		this.speed = speed;
		new PotionTrail(this.getBukkitEntity()).start();

		FoxBukkit.instance.getServer().getLogger().log(Level.WARNING, thrower.getName() + " used meteor potion at (" + thrower.locX + "," + thrower.locY + "," + thrower.locZ + ")");
	}

	@Override
	protected boolean hitBlock(Block hitBlock, BlockFace sideHit, Location hitLocation) {
		final double radiusSq = radius * radius;

		final Entity thisBukkitEntity = getBukkitEntity();
		final World world = thisBukkitEntity.getWorld();

		FoxBukkit.instance.playerHelper.sendPacketToPlayersAround(hitLocation, 64, new PacketPlayOutExplosion(hitLocation.getX(), hitLocation.getY(), hitLocation.getZ(), -1.0f, new ArrayList<BlockPosition>(), null));
		Utils.makeSound(hitLocation, "random.explode", 4.0F, (float) ((1.0 + (Math.random() - Math.random()) * 0.2) * 0.7));

		final Location min = hitLocation.clone().subtract(radius, radius, radius);
		final Location max = hitLocation.clone().add(radius + 1, radius + 1, radius + 1);

		for (Entity entity : thisBukkitEntity.getNearbyEntities(radius, radius, radius)) {
			if (hitLocation.distanceSquared(entity.getLocation()) > radiusSq)
				continue;

			entity.setVelocity(randvecUp().multiply(speed));
		}

		for (int x = min.getBlockX(); x <= max.getBlockX(); ++x) {
			for (int y = min.getBlockY(); y <= max.getBlockY(); ++y) {
				for (int z = min.getBlockZ(); z <= max.getBlockZ(); ++z) {
					final Location location = new Location(world, x + 0.5, y + 0.5, z + 0.5);
					final Block block = location.getBlock();
					location.subtract(hitLocation);
					if (location.lengthSquared() > radiusSq)
						continue;

					final int typeId = block.getTypeId();
					if (typeId == 0 || typeId == 7)
						continue;

					final byte data = block.getData();

					if (thrower != null && FoxBukkit.instance.logBlockConsumer != null)
						FoxBukkit.instance.logBlockConsumer.queueBlockBreak(Actor.actorFromEntity(thrower.getBukkitEntity()), block.getState());
					block.setTypeIdAndData(0, (byte) 0, true);

					final Location fbloc = location.multiply(0.5);
					fbloc.setY(0);
					fbloc.add(hitLocation);

					final EntityFallingBlock notchEntity = Utils.spawnFallingBlock(fbloc, typeId, data);
					final Entity entity = notchEntity.getBukkitEntity();

					//entity.setVelocity(location.toVector().add(new Vector(0, radius, 0)).multiply(speed/radius));
					entity.setVelocity(randvecUp().multiply(speed));
				}
			}
		}

		return true;
	}

	private Vector randvecUp() {
		final Vector vel = Utils.randvec();
		vel.setY(Math.abs(vel.getY()));
		return vel;
	}

	public static class PotionTrail extends FBEffect.PotionTrail {
		public PotionTrail(Entity entity) {
			super(entity);
		}

		@Override
		protected void renderEffect(Location location) {
			SpawnUtils.makeParticles(location, new Vector(1, 1, 1), 0, 20, "flame");
			if (Math.random() < 0.05)
				Utils.makeSound(location, "mob.ghast.fireball", .5F, 1F);
			Utils.makeSound(location, "random.breath", .5F, .1F);
		}
	}
}
