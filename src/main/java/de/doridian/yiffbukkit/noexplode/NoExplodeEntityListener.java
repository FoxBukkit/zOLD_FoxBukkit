package de.doridian.yiffbukkit.noexplode;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;

public class NoExplodeEntityListener implements Listener
{
	public static NoExplode plugin;
	public NoExplodeEntityListener(NoExplode instance)
	{
		plugin = instance;

		plugin.yiffbukkit.getServer().getPluginManager().registerEvents(this, plugin.yiffbukkit);
	}
	
	private String getEntityName(EntityEvent event)
	{
		Entity ent = event.getEntity();
		if(ent == null) return "";
		@SuppressWarnings("rawtypes")
		Class cls = ent.getClass();
		if(cls == null) return "";
		String name = cls.getName();
		if(name == null) return "";
		return name;
	}

	@EventHandler(event = ExplosionPrimeEvent.class, priority = EventPriority.NORMAL)
	public void onExplosionPrimed(ExplosionPrimeEvent event)
	{
		String name = getEntityName(event);
		if (!plugin.explodetnt && name.contains("CraftTNTPrimed"))
		{
			event.setFire(false);
			event.setRadius(0);
		}
		else if (!plugin.damagecreeper && name.contains("CraftCreeper"))
		{
			event.setFire(false);
			event.setRadius(0);
		}
		else if (!plugin.explodeghast && name.contains("CraftFireball"))
		{
			event.setFire(false);
			event.setRadius(0);
		}
	}

	@EventHandler(event = EntityExplodeEvent.class, priority = EventPriority.NORMAL)
	public void onEntityExplode(EntityExplodeEvent event)
	{
		String name = getEntityName(event);
		if (!plugin.explodetnt && name.contains("CraftTNTPrimed"))
		{
			event.setCancelled(true);
		}
		else if (!plugin.explodecreeper && name.contains("CraftCreeper"))
		{
			event.setCancelled(true);
		}
		else if (!plugin.explodeghast && name.contains("CraftFireball"))
		{
			event.setCancelled(true);
		}
	}
}
