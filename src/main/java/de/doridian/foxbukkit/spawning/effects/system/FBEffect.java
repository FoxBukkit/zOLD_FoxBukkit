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
package de.doridian.foxbukkit.spawning.effects.system;

import de.doridian.foxbukkit.core.FoxBukkit;
import de.doridian.foxbukkit.main.FoxBukkitCommandException;
import de.doridian.foxbukkit.main.util.ScheduledTask;
import de.doridian.foxbukkit.spawning.effects.Jetpack;
import de.doridian.foxbukkit.spawning.effects.LSD;
import de.doridian.foxbukkit.spawning.effects.Rage;
import de.doridian.foxbukkit.spawning.effects.Redrum;
import de.doridian.foxbukkit.spawning.effects.Rocket;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public abstract class FBEffect extends ScheduledTask {
	static final class DeathListener implements Listener {
		@EventHandler
		public void onEntityDeath(EntityDeathEvent event) {
			final FBEffect effect = effects.remove(event.getEntity());
			if (effect == null)
				return;

			effect.stop();
		}

		@EventHandler
		public void onPlayerQuit(PlayerQuitEvent event) {
			final FBEffect effect = effects.remove(event.getPlayer());
			if (effect == null)
				return;

			effect.stop();
		}
	}

	private static final Map<Entity, FBEffect> effects = new HashMap<>();
	private static final Map<String, Class<? extends FBEffect>> effectClasses = new HashMap<>();
	static {
		addEffectClass(Rocket.class);
		addEffectClass(LSD.class);
		addEffectClass(Rage.class);
		addEffectClass(Redrum.class);
		addEffectClass(Jetpack.class);
		Bukkit.getPluginManager().registerEvents(new DeathListener(), FoxBukkit.instance);
	}

	private static void addEffectClass(Class<? extends FBEffect> effectClass) {
		effectClasses.put(effectClass.getAnnotation(EffectProperties.class).name(), effectClass);
	}

	protected final Entity entity;

	public FBEffect(Entity entity) {
		super(FoxBukkit.instance);
		this.entity = entity;
	}


	public abstract void start();

	public void forceStart() {
		start();
	}

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


	public static boolean effectExists(String effect) {
		return effectClasses.containsKey(effect);
	}

	public static EffectProperties getEffectProperties(String effect) {
		if (!effectExists(effect))
			return null;

		return effectClasses.get(effect).getAnnotation(EffectProperties.class);
	}

	public static FBEffect create(String effect, Entity entity) throws FoxBukkitCommandException {
		if (!effectExists(effect))
			throw new FoxBukkitCommandException("Effect '"+effect+"' not found.");

		return create(effectClasses.get(effect), entity);
	}

	public static FBEffect createTrail(String effect, Entity entity) throws FoxBukkitCommandException {
		if (!effectExists(effect))
			throw new FoxBukkitCommandException("Effect '"+effect+"' not found.");

		return createTrail(effectClasses.get(effect), entity);
	}

	@SuppressWarnings("unchecked")
	public static FBEffect createTrail(Class<? extends FBEffect> effectClass, Entity entity) throws FoxBukkitCommandException {
		if (!effectClass.getAnnotation(EffectProperties.class).potionTrail())
			return new NullEffect();

		//Class.forName(effectClass.getCanonicalName()+".PotionTrail");
		for (Class<?> enclosedClass : effectClass.getDeclaredClasses()) {
			if (enclosedClass.getSimpleName().equals("PotionTrail"))
				return create((Class<? extends FBEffect>) enclosedClass, entity);
		}

		throw new FoxBukkitCommandException("Effect doesn't have a PotionTrail enclosed class but is flagged as having one.");
	}

	public static FBEffect create(Class<? extends FBEffect> effectClass, Entity entity) throws FoxBukkitCommandException {
		if (effects.containsKey(entity))
			throw new FoxBukkitCommandException("Entity already has an effect.");

		try {
			final FBEffect effect = effectClass.getConstructor(Entity.class).newInstance(entity);
			effects.put(entity, effect);
			return effect;
		} catch (InstantiationException e) {
			throw new FoxBukkitCommandException("Effect cannot be instantiated.", e);
		} catch (IllegalAccessException e) {
			throw new FoxBukkitCommandException("Effect constructor not accessible.", e);
		} catch (InvocationTargetException e) {
			final Throwable cause = e.getCause();

			if (cause instanceof FoxBukkitCommandException)
				throw (FoxBukkitCommandException) cause;

			if (cause instanceof RuntimeException)
				throw (RuntimeException) cause;

			if (cause instanceof Error)
				throw (Error) cause;

			throw new FoxBukkitCommandException("Exception caught in effect constructor.", cause);
		} catch (NoSuchMethodException e) {
			throw new FoxBukkitCommandException("Effect has no suitable constructor.", e);
		}
	}

	public static abstract class PotionTrail extends FBEffect {
		private static final int STEPS = 5;

		Location lastLocation = null;
		public PotionTrail(Entity entity) {
			super(entity);
		}

		@Override
		public void runEffect() {
			final Location currentLocation = entity.getLocation();

			if (lastLocation != null) {
				Location diff = currentLocation.clone().subtract(lastLocation);
				Location location = lastLocation;
				diff.multiply(1.0 / STEPS);

				for (int i = 0; i < STEPS; ++i) {
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
