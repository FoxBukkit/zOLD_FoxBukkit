package de.doridian.yiffbukkitsplit.effects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkit.spawning.fakeentity.FakeEntity;
import de.doridian.yiffbukkit.spawning.fakeentity.FakeExperienceOrb;

@EffectProperties(
		name = "rocket",
		potionColor = 12,
		radius = 3
)
public class Rocket extends YBEffect {
	private int i = 0;
	private List<Entity> toRemove = new ArrayList<Entity>();
	private Vector velocity = entity.getVelocity();
	private double maxHeight;

	public Rocket(Entity entity) {
		super(entity);
	}

	@Override
	public void start() {
		if (!(entity instanceof LivingEntity))
			return;

		if (entity instanceof Player)
			return;

		maxHeight = entity.getLocation().getY()+32;

		scheduleSyncRepeating(0, 1);
	}

	@Override
	public void run() {
		if (i == 101) {
			for (Entity e : toRemove) {
				e.remove();
			}
			done();
			return;
		}

		velocity = velocity.add(new Vector(0,0.1,0));
		entity.setVelocity(velocity);
		final Location currentLocation = entity.getLocation();
		//for (int data = 0; data < 16; ++data)
		final World currentWorld = currentLocation.getWorld();
		currentWorld.playEffect(currentLocation, Effect.SMOKE, 4);
		currentWorld.playEffect(currentLocation, Effect.EXTINGUISH, 0);

		++i;
		if (i == 100 || currentLocation.getY() >= maxHeight) {
			i = 101;
			entity.remove();
			for (Player player : currentWorld.getPlayers()) {
				final Location playerLocation = player.getLocation();
				if (currentLocation.distanceSquared(playerLocation) > 64*64)
					continue;

				final Location modifiedLocation = playerLocation.add(currentLocation).multiply(0.5);
				player.playEffect(modifiedLocation, Effect.ZOMBIE_DESTROY_DOOR, 0);
			}
			cancel();

			for (int i = 0; i < 100; ++i) {
				final FakeEntity fakeEntity = new FakeExperienceOrb(currentLocation, 1);
				fakeEntity.send();
				fakeEntity.teleport(currentLocation);
				fakeEntity.setVelocity(Utils.randvec());
				toRemove.add(fakeEntity);
			}

			scheduleSyncDelayed(60);
		}
	}
}