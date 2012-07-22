package de.doridian.yiffbukkit.spawning.effects.system;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.util.ScheduledTask;
import de.doridian.yiffbukkit.spawning.effects.LSD;
import de.doridian.yiffbukkit.spawning.effects.Rage;
import de.doridian.yiffbukkit.spawning.effects.Redrum;
import de.doridian.yiffbukkit.spawning.effects.Rocket;
import de.doridian.yiffbukkitsplit.YiffBukkit;

public abstract class YBEffect extends ScheduledTask {
	static final class DeathListener implements Listener {
		@EventHandler
		public void onEntityDeath(EntityDeathEvent event) {
			final YBEffect effect = effects.remove(event.getEntity());
			if (effect == null)
				return;

			effect.stop();
		}

		@EventHandler
		public void onPlayerQuit(PlayerQuitEvent event) {
			final YBEffect effect = effects.remove(event.getPlayer());
			if (effect == null)
				return;

			effect.stop();
		}
	}

	private static final Map<Entity, YBEffect> effects = new HashMap<Entity, YBEffect>();
	private static final Map<String, Class<? extends YBEffect>> effectClasses = new HashMap<String, Class<? extends YBEffect>>();
	static {
		addEffectClass(Rocket.class);
		addEffectClass(LSD.class);
		addEffectClass(Rage.class);
		addEffectClass(Redrum.class);
		Bukkit.getPluginManager().registerEvents(new DeathListener(), YiffBukkit.instance);
	}

	private static void addEffectClass(Class<? extends YBEffect> effectClass) {
		effectClasses.put(effectClass.getAnnotation(EffectProperties.class).name(), effectClass);
	}

	protected final Entity entity;

	public YBEffect(Entity entity) {
		super(YiffBukkit.instance);
		this.entity = entity;
	}


	public abstract void start();

	public final void stop() {
		cancel();
		done();
		cleanup();
	}

	protected void cleanup() { }


	protected final void done() {
		effects.remove(entity);
	}

	public final void run() {
		if (!entity.isValid()) {
			stop();
			return;
		}

		runEffect();
	}

	protected abstract void runEffect();

	public static EffectProperties getEffectProperties(String effect) {
		Class<? extends YBEffect> effectClass = effectClasses.get(effect);

		return effectClass.getAnnotation(EffectProperties.class);
	}
	public static YBEffect create(String effect, Entity entity) throws YiffBukkitCommandException {
		if (!effectClasses.containsKey(effect))
			throw new YiffBukkitCommandException("Effect '"+effect+"' not found.");

		return create(effectClasses.get(effect), entity);
	}

	public static YBEffect createTrail(String effect, Entity entity) throws YiffBukkitCommandException {
		if (!effectClasses.containsKey(effect))
			throw new YiffBukkitCommandException("Effect '"+effect+"' not found.");

		return createTrail(effectClasses.get(effect), entity);
	}

	@SuppressWarnings("unchecked")
	public static YBEffect createTrail(Class<? extends YBEffect> effectClass, Entity entity) throws YiffBukkitCommandException {
		if (!effectClass.getAnnotation(EffectProperties.class).potionTrail())
			return new NullEffect();

		//Class.forName(effectClass.getCanonicalName()+".PotionTrail");
		for (Class<?> enclosedClass : effectClass.getDeclaredClasses()) {
			if (enclosedClass.getSimpleName().equals("PotionTrail"))
				return create((Class<? extends YBEffect>) enclosedClass, entity);
		}

		throw new YiffBukkitCommandException("Effect doesn't have a PotionTrail enclosed class but is flagged as having one.");
	}

	public static YBEffect create(Class<? extends YBEffect> effectClass, Entity entity) throws YiffBukkitCommandException {
		if (effects.containsKey(entity))
			throw new YiffBukkitCommandException("Entity already has an effect.");

		try {
			final YBEffect effect = effectClass.getConstructor(Entity.class).newInstance(entity);
			effects.put(entity, effect);
			return effect;
		} catch (InstantiationException e) {
			throw new YiffBukkitCommandException("Effect cannot be instantiated.", e);
		} catch (IllegalAccessException e) {
			throw new YiffBukkitCommandException("Effect constructor not accessible.", e);
		} catch (InvocationTargetException e) {
			final Throwable cause = e.getCause();

			if (cause instanceof YiffBukkitCommandException)
				throw (YiffBukkitCommandException) cause;

			if (cause instanceof RuntimeException)
				throw (RuntimeException) cause;

			if (cause instanceof Error)
				throw (Error) cause;

			throw new YiffBukkitCommandException("Exception caught in effect constructor.", cause);
		} catch (NoSuchMethodException e) {
			throw new YiffBukkitCommandException("Effect has no suitable constructor.", e);
		}
	}

	public static abstract class PotionTrail extends YBEffect {
		Location lastLocation = null;
		public PotionTrail(Entity entity) {
			super(entity);
		}

		@Override
		public void runEffect() {
			final Location currentLocation = entity.getLocation();

			if (lastLocation != null) {
				Location location = currentLocation.clone();
				Location diff = lastLocation.subtract(currentLocation);
				diff.multiply(1.0/5);

				for (int i = 0; i < 5; ++i) {
					renderEffect(location);
					location.add(diff);
				}
			}

			lastLocation = currentLocation;
		}

		protected abstract void renderEffect(Location location);

		@Override
		public void start() {
			scheduleSyncRepeating(0, 1);
		}
	}
}
