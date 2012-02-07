package de.doridian.yiffbukkit.spawning.sheep;

import de.doridian.yiffbukkitsplit.YiffBukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.util.HashSet;
import java.util.Set;

public class TrapSheep implements Runnable {
	public static class TrapSheepEntityListener implements Listener {
		public TrapSheepEntityListener(YiffBukkit plugin) {
			plugin.getServer().getPluginManager().registerEvents(entityListener, plugin);
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onEntityDamage(EntityDamageEvent event) {
			if (event.getCause() != DamageCause.ENTITY_ATTACK)
				return;

			if (!trapSheeps.contains(event.getEntity()))
				return;

			final Entity entity = event.getEntity();

			entity.getWorld().strikeLightning(entity.getLocation());
		}
	}

	private final YiffBukkit plugin;
	protected final Sheep sheep;

	private static Set<Entity> trapSheeps = new HashSet<Entity>();
	private static Listener entityListener = null;
	private final int taskId;

	public TrapSheep(YiffBukkit plugin, Sheep sheep) {
		this.plugin = plugin;
		this.sheep = sheep;

		trapSheeps.add(sheep);

		taskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 200);

		if (entityListener != null)
			return;

		entityListener = new TrapSheepEntityListener(plugin);
	}

	@Override
	public void run() {
		if (sheep.isDead() || sheep.isSheared()) {
			plugin.getServer().getScheduler().cancelTask(taskId);
			trapSheeps.remove(sheep);
			return;
		}
	}
}
