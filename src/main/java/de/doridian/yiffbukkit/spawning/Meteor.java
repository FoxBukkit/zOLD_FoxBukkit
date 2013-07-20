package de.doridian.yiffbukkit.spawning;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkit.spawning.effects.system.YBEffect;
import de.doridian.yiffbukkit.spawning.potions.CustomPotion;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import net.minecraft.server.v1_6_R2.EntityFallingBlock;
import net.minecraft.server.v1_6_R2.EntityPlayer;
import net.minecraft.server.v1_6_R2.Packet60Explosion;
import net.minecraft.server.v1_6_R2.WorldServer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_6_R2.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.Collections;

public class Meteor extends CustomPotion {
	private final double radius;
	private final double speed;

	public Meteor(Location location, EntityPlayer thrower, double radius, double speed) throws YiffBukkitCommandException {
		super(location, 8, thrower); // TODO: pick different potionId
		this.radius = radius;
		this.speed = speed;
		new PotionTrail(this.getBukkitEntity()).start();
	}

	@Override
	protected boolean hitBlock(Block hitBlock, BlockFace sideHit, Location hitLocation) throws YiffBukkitCommandException {
		final double radiusSq = radius * radius;

		final Entity thisBukkitEntity = getBukkitEntity();
		final World world = thisBukkitEntity.getWorld();

		YiffBukkit.instance.playerHelper.sendPacketToPlayersAround(hitLocation, 64, new Packet60Explosion(hitLocation.getX(), hitLocation.getY(), hitLocation.getZ(), -1.0f, Collections.emptyList(), null));
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

					if (thrower != null && YiffBukkit.instance.logBlockConsumer != null)
						YiffBukkit.instance.logBlockConsumer.queueBlockBreak(thrower.getName(), block.getState());
					block.setTypeIdAndData(0, (byte) 0, true);

					final WorldServer notchWorld = ((CraftWorld) world).getHandle();

					final Location fbloc = location.multiply(0.5);
					fbloc.setY(0);
					fbloc.add(hitLocation);

					final EntityFallingBlock notchEntity = new EntityFallingBlock(notchWorld, fbloc.getX(), fbloc.getY(), fbloc.getZ(), typeId, data);

					// This disables the first tick code, which takes care of removing the original block etc.
					notchEntity.c = 1; // v1_6_R2

					// Do not drop an item if placing a block fails
					notchEntity.dropItem = false;

					notchWorld.addEntity(notchEntity);

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

	public static class PotionTrail extends YBEffect.PotionTrail {
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
