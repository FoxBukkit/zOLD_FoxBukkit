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
package de.doridian.yiffbukkit.spawning.fakeentity;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import de.doridian.yiffbukkit.spawning.SpawnUtils;

public class FakeEntityParticleSpawner extends FakeEntity {
	private final Vector scatter;
	private final double particleSpeed;
	private final int numParticles;
	private final String particleName;

	public FakeEntityParticleSpawner(Location location, Vector scatter, double particleSpeed, int numParticles, String particleName) {
		super(location);
		this.scatter = scatter;
		this.particleSpeed = particleSpeed;
		this.numParticles = numParticles;
		this.particleName = particleName;
	}

	@Override
	public void send(Player player) {
		SpawnUtils.makeParticles(player, location, scatter, particleSpeed, numParticles, particleName);
	}

	@Override
	public void setVelocity(Vector velocity) {
	}
}
