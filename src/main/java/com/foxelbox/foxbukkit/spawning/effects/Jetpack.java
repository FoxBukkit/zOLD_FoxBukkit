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
package com.foxelbox.foxbukkit.spawning.effects;

import com.foxelbox.foxbukkit.core.FoxBukkit;
import com.foxelbox.foxbukkit.main.util.ScheduledTask;
import com.foxelbox.foxbukkit.main.util.Utils;
import com.foxelbox.foxbukkit.spawning.SpawnUtils;
import com.foxelbox.foxbukkit.spawning.effects.system.EffectProperties;
import com.foxelbox.foxbukkit.spawning.effects.system.FBEffect;
import com.foxelbox.foxbukkit.spawning.fakeentity.FakeEntity;
import com.foxelbox.foxbukkit.spawning.fakeentity.FakeExperienceOrb;
import net.minecraft.server.v1_7_R3.PacketPlayOutExplosion;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@EffectProperties(
		name = "jetpack",
		potionColor = 12,
		potionTrail = true
)
public class Jetpack extends FBEffect.PotionTrail {
	private int i = 0;
	private final List<Entity> toRemove = new ArrayList<>();
	private Vector velocity = new Vector();
	private Player player;

	public Jetpack(Entity entity) {
		super(entity);
	}

	@Override
	public void start() {
		forceStart();
	}

	@Override
	public void forceStart() {
		if (!(entity instanceof Player)) {
			done();
			return;
		}

		player = (Player) entity;

		scheduleSyncRepeating(0, 1);
	}

	@Override
	protected void renderEffect(Location location) {
		if (!active()) {
			return;
		}
		SpawnUtils.makeParticles(location, new Vector(), 0.05, 3, "fireworksSpark");
	}

	@Override
	public void runEffect() {
		super.runEffect();
		if (!active()) {
			velocity = velocity.multiply(0.95);
			return;
		}

		velocity = velocity.add(new Vector(0, 0.01, 0.01));
		if (velocity.lengthSquared() > 2)
			velocity = velocity.normalize().multiply(2);

		final Location currentLocation = entity.getLocation();
		entity.setVelocity(Utils.toWorldAxis(currentLocation, velocity));
		//for (int data = 0; data < 16; ++data)
		final World currentWorld = currentLocation.getWorld();
		currentWorld.playEffect(currentLocation, Effect.EXTINGUISH, 0);

		++i;
		if (i == 3000) {
			done();
			cancel();

			FoxBukkit.instance.playerHelper.sendPacketToPlayersAround(currentLocation, 64, new PacketPlayOutExplosion(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ(), -1.0f, Collections.emptyList(), null));
			Utils.makeSound(currentLocation, "random.explode", 4.0F, (float) ((1.0 + (Math.random() - Math.random()) * 0.2) * 0.7));

			for (int i = 0; i < 100; ++i) {
				final FakeEntity fakeEntity = new FakeExperienceOrb(currentLocation, 1);
				fakeEntity.send();
				fakeEntity.teleport(currentLocation);
				fakeEntity.setVelocity(Utils.randvec());
				toRemove.add(fakeEntity);
			}

			cleanup();
		}
	}

	private boolean active() {
		return player.isSneaking() && !player.isOnGround();
	}

	@Override
	protected void cleanup() {
		new ScheduledTask(FoxBukkit.instance) {
			@Override
			public void run() {
				for (Entity e : toRemove) {
					e.remove();
				}
			}
		}.scheduleSyncDelayed(60);
	}

	public static class PotionTrail extends FBEffect.PotionTrail {
		public PotionTrail(Entity entity) {
			super(entity);
		}

		@Override
		protected void renderEffect(Location location) {
			SpawnUtils.makeParticles(location, new Vector(), 0.05, 3, "fireworksSpark");
		}
	}
}
