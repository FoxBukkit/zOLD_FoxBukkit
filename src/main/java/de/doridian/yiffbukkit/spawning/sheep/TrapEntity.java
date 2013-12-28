package de.doridian.yiffbukkit.spawning.sheep;

import de.doridian.yiffbukkit.core.YiffBukkit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.util.HashSet;
import java.util.Set;

public class TrapEntity implements Runnable {
	public static class TrapSheepEntityListener implements Listener {
		public TrapSheepEntityListener() {
			Bukkit.getPluginManager().registerEvents(this, YiffBukkit.instance);
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onEntityDamage(EntityDamageEvent event) {
			if (event.getCause() != DamageCause.ENTITY_ATTACK)
				return;

			if (!trapEntities.contains(event.getEntity()))
				return;

			final Entity entity = event.getEntity();

			entity.getWorld().strikeLightning(entity.getLocation());
		}
	}

	protected final Entity entity;

	private static Set<Entity> trapEntities = new HashSet<>();
	private static Listener entityListener = null;
	private final int taskId;

	public TrapEntity(YiffBukkit plugin, Entity entity) {
		this.entity = entity;

		trapEntities.add(entity);

		taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 200);

		if (entityListener == null) {
			entityListener = new TrapSheepEntityListener();
		}
	}

	@Override
	public void run() {
		if (canBeRemoved()) {
			Bukkit.getScheduler().cancelTask(taskId);
			trapEntities.remove(entity);
		}
	}

	private boolean canBeRemoved() {
		if (!entity.isValid())
			return true;

		//noinspection SimplifiableIfStatement
		if (!(entity instanceof Sheep))
			return false;

		return ((Sheep) entity).isSheared();
	}
}
