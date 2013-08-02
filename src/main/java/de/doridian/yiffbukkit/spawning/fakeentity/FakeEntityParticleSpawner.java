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
