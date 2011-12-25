package de.doridian.yiffbukkit.sheep;

import de.doridian.yiffbukkit.YiffBukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Sheep;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityListener;

import java.util.HashSet;
import java.util.Set;

public class TrapSheep implements Runnable {
	private final YiffBukkit plugin;
	protected final Sheep sheep;

	private static Set<Entity> trapSheeps = new HashSet<Entity>();
	private static EntityListener entityListener = null;
	private final int taskId;

	public TrapSheep(YiffBukkit plugin, Sheep sheep) {
		this.plugin = plugin;
		this.sheep = sheep;

		trapSheeps.add(sheep);

		taskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 200);

		if (entityListener != null)
			return;

		entityListener = new EntityListener() {
			@Override
			public void onEntityDamage(EntityDamageEvent event) {
				if (event.getCause() != DamageCause.ENTITY_ATTACK)
					return;

				if (!trapSheeps.contains(event.getEntity()))
					return;

				final Entity entity = event.getEntity();

				entity.getWorld().strikeLightning(entity.getLocation());
			}
		};
		plugin.getServer().getPluginManager().registerEvent(Type.ENTITY_DAMAGE, entityListener, Priority.Monitor, plugin);
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
