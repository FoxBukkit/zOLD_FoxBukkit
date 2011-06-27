package de.doridian.yiffbukkit.noexplode;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class NoExplodeEntityListener extends EntityListener
{
	public static NoExplode plugin;
	public NoExplodeEntityListener(NoExplode instance)
	{
		plugin = instance;
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
