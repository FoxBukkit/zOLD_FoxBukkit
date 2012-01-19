package de.doridian.yiffbukkit.noexplode;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;

public class NoExplodeEntityListener implements Listener {
	private final NoExplode plugin;

	public NoExplodeEntityListener(NoExplode instance) {
		plugin = instance;

		plugin.yiffBukkit.getServer().getPluginManager().registerEvents(this, plugin.yiffBukkit);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onExplosionPrimed(ExplosionPrimeEvent event) {
		Entity entity = event.getEntity();
		if (!plugin.explodetnt && entity instanceof TNTPrimed) {
			event.setFire(false);
			event.setRadius(0);
		}
		else if (!plugin.damagecreeper && entity instanceof Creeper) {
			event.setFire(false);
			event.setRadius(0);
		}
		else if (!plugin.explodeghast && entity instanceof Fireball) {
			event.setFire(false);
			event.setRadius(0);
		}
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		Entity entity = event.getEntity();
		if (!plugin.explodetnt && entity instanceof TNTPrimed) {
			event.setCancelled(true);
		}
		else if (!plugin.explodecreeper && entity instanceof Creeper) {
			event.setCancelled(true);
		}
		else if (!plugin.explodeghast && entity instanceof Fireball) {
			event.setCancelled(true);
		}
	}
}
