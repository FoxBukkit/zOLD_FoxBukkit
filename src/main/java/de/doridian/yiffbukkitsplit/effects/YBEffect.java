package de.doridian.yiffbukkitsplit.effects;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Entity;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.util.ScheduledTask;
import de.doridian.yiffbukkitsplit.YiffBukkit;

public abstract class YBEffect extends ScheduledTask {
	private static final Map<Entity, YBEffect> effects = new HashMap<Entity, YBEffect>();
	private static final Map<String, Class<? extends YBEffect>> effectClasses = new HashMap<String, Class<? extends YBEffect>>();
	static {
		addEffectClass(Rocket.class);
		addEffectClass(LSD.class);
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

	public void stop() {
		cancel();
		done();
	}

	protected void done() {
		effects.remove(entity);
	}

	public static EffectProperties getEffectProperties(String effect) {
		Class<? extends YBEffect> effectClass = effectClasses.get(effect);

		return effectClass.getAnnotation(EffectProperties.class);
	}
	public static void create(String effect, Entity entity) throws YiffBukkitCommandException {
		if (!effectClasses.containsKey(effect))
			throw new YiffBukkitCommandException("Effect '"+effect+"' not found.");

		create(effectClasses.get(effect), entity);
	}

	public static void create(Class<? extends YBEffect> effectClass, Entity entity) throws YiffBukkitCommandException {
		if (effects.containsKey(entity))
			throw new YiffBukkitCommandException("Entity already has an effect.");

		try {
			final YBEffect effect = effectClass.getConstructor(Entity.class).newInstance(entity);
			effects.put(entity, effect);
			effect.start();
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
}
