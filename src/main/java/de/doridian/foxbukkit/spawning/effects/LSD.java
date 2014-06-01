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
package de.doridian.foxbukkit.spawning.effects;

import de.doridian.foxbukkit.main.util.Utils;
import de.doridian.foxbukkit.spawning.effects.system.EffectProperties;
import de.doridian.foxbukkit.spawning.effects.system.FBEffect;
import de.doridian.foxbukkit.spawning.fakeentity.FakeEntity;
import de.doridian.foxbukkit.spawning.fakeentity.FakeVehicle;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.LinkedList;
import java.util.Queue;

@EffectProperties(
		name = "lsd",
		potionColor = 3,
		radius = 3
)
public class LSD extends FBEffect {
	private int i = 0;
	Queue<Entity> toRemove = new LinkedList<>();
	private static final int[] randomCrap = { 60, 61, 62, 63, 64, 65, 72, 73, 90, };

	public LSD(Entity entity) {
		super(entity);
	}

	@Override
	public void start() {
		if (!(entity instanceof Player)) {
			done();
			return;
		}

		scheduleSyncRepeating(0, 1);
	}

	@Override
	public void runEffect() {
		final Player player = (Player) entity;
		// TODO: area/direct hit with different lengths
		if (i == 500) {
			for (Entity e : toRemove) {
				e.remove();
			}
			done();
			cancel();
			return;
		}

		for (int j = 0; j < 5; ++j) {
			final Location currentLocation = player.getLocation().clone().add(Math.random()*10-5, -1, Math.random()*10-5);
			final FakeEntity fakeEntity = new FakeVehicle(currentLocation, randomCrap[(int) Math.floor(Math.random()*randomCrap.length)]);
			fakeEntity.send(player);
			fakeEntity.teleport(currentLocation);
			fakeEntity.setVelocity(new Vector(0,.3+Math.random(),0));
			toRemove.add(fakeEntity);
		}
		for (int j = 0; j < 3; ++j) {
			final Vector velocity = Utils.randvec().multiply(1+Math.random());
			final Location currentLocation = player.getEyeLocation().subtract(velocity.clone().multiply(20));
			final FakeEntity fakeEntity = new FakeVehicle(currentLocation, 72);
			fakeEntity.send(player);
			fakeEntity.teleport(currentLocation);
			fakeEntity.setVelocity(velocity);
			toRemove.add(fakeEntity);
		}
		while (toRemove.size() > 240) {
			toRemove.poll().remove();
		}

		++i;
	}
}