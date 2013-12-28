package de.doridian.yiffbukkit.spawning.effects;

import de.doridian.yiffbukkit.core.YiffBukkit;
import de.doridian.yiffbukkit.main.util.ScheduledTask;
import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkit.spawning.SpawnUtils;
import de.doridian.yiffbukkit.spawning.effects.system.EffectProperties;
import de.doridian.yiffbukkit.spawning.effects.system.YBEffect;
import de.doridian.yiffbukkit.spawning.fakeentity.FakeEntity;
import de.doridian.yiffbukkit.spawning.fakeentity.FakeExperienceOrb;
import net.minecraft.server.v1_7_R1.PacketPlayOutExplosion;
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
public class Jetpack extends YBEffect.PotionTrail {
	private int i = 0;
	private List<Entity> toRemove = new ArrayList<>();
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

		velocity = velocity.add(new Vector(0.01, 0.01, 0));
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

			YiffBukkit.instance.playerHelper.sendPacketToPlayersAround(currentLocation, 64, new PacketPlayOutExplosion(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ(), -1.0f, Collections.emptyList(), null));
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
		new ScheduledTask(YiffBukkit.instance) {
			@Override
			public void run() {
				for (Entity e : toRemove) {
					e.remove();
				}
			}
		}.scheduleSyncDelayed(60);
	}

	public static class PotionTrail extends YBEffect.PotionTrail {
		public PotionTrail(Entity entity) {
			super(entity);
		}

		@Override
		protected void renderEffect(Location location) {
			SpawnUtils.makeParticles(location, new Vector(), 0.05, 3, "fireworksSpark");
		}
	}
}
