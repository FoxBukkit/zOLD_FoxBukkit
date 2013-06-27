package de.doridian.yiffbukkit.spawning.effects;

import de.doridian.yiffbukkit.main.util.ScheduledTask;
import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkit.spawning.SpawnUtils;
import de.doridian.yiffbukkit.spawning.effects.system.EffectProperties;
import de.doridian.yiffbukkit.spawning.effects.system.YBEffect;
import de.doridian.yiffbukkit.spawning.fakeentity.FakeEntity;
import de.doridian.yiffbukkit.spawning.fakeentity.FakeExperienceOrb;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import net.minecraft.server.v1_5_R3.ItemStack;
import net.minecraft.server.v1_5_R3.Packet60Explosion;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@EffectProperties(
		name = "rocket",
		potionColor = 12,
		potionTrail = true
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
		if (!(entity instanceof LivingEntity)) {
			done();
			return;
		}

		if (entity instanceof Player) {
			done();
			return;
		}

		// TODO: area/direct hit with different heights
		maxHeight = entity.getLocation().getY() + 32;

		scheduleSyncRepeating(0, 1);
	}

	static Map<EntityType, String> fireworkTypes = new EnumMap<EntityType, String>(EntityType.class);
	{
		fireworkTypes.put(EntityType.SPIDER, "1e1b1b,434343/Type=1/Trail/Fade=b3312c");
		fireworkTypes.put(EntityType.CREEPER, "41cd34,3b511a,ababab/Type=3/Trail");
		fireworkTypes.put(EntityType.SQUID, "0,001010/Type=0/Trail");
		fireworkTypes.put(EntityType.CAVE_SPIDER, "1e1b1b,304343/Type=0/Trail/Fade=b3312c");
		fireworkTypes.put(EntityType.PIG, "f09090/Type=0/Trail/Fade=b3312c");
		fireworkTypes.put(EntityType.PIG_ZOMBIE, "f09090/Type=2/Trail/Fade=b3312c");
		fireworkTypes.put(EntityType.ENDERMAN, "5b1e66,a035b2/Type=2/Trail/Fade=a035b2,5b1e66/Flicker");
	}

	@Override
	public void runEffect() {
		velocity = velocity.add(new Vector(0, 0.1, 0));
		entity.setVelocity(velocity);
		final Location currentLocation = entity.getLocation();
		//for (int data = 0; data < 16; ++data)
		final World currentWorld = currentLocation.getWorld();
		currentWorld.playEffect(currentLocation, Effect.SMOKE, 4);
		currentWorld.playEffect(currentLocation, Effect.EXTINGUISH, 0);

		++i;
		if (i == 100 || currentLocation.getY() >= maxHeight) {
			done();
			cancel();
			entity.remove();

			final String fireworkType = fireworkTypes.get(entity.getType());
			if (fireworkType != null) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(YiffBukkit.instance, new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub

						final ItemStack fireworks = SpawnUtils.makeFireworks(fireworkType);
						SpawnUtils.explodeFirework(currentLocation, fireworks);
					}
				});
				return;
			}

			YiffBukkit.instance.playerHelper.sendPacketToPlayersAround(currentLocation, 64, new Packet60Explosion(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ(), -1.0f, Collections.emptyList(), null));
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

	@Override
	protected void cleanup() {
		new ScheduledTask(YiffBukkit.instance) {
			@Override
			public void run() {
				for (Entity e : toRemove) {
					e.remove();
				}
				return;
			}
		}.scheduleSyncDelayed(60);
	}

	public static class PotionTrail extends YBEffect.PotionTrail {
		public PotionTrail(Entity entity) {
			super(entity);
		}

		@Override
		protected void renderEffect(Location location) {
			location.getWorld().playEffect(location, Effect.SMOKE, 4);
		}
	}
}
