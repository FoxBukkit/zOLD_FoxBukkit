package de.doridian.yiffbukkitsplit.effects;

import java.util.LinkedList;
import java.util.Queue;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkit.spawning.fakeentity.FakeEntity;
import de.doridian.yiffbukkit.spawning.fakeentity.FakeVehicle;

@EffectProperties(
		name = "lsd",
		potionColor = 3,
		radius = 3
)
public class LSD extends YBEffect {
	private int i = 0;
	Queue<Entity> toRemove = new LinkedList<Entity>();
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