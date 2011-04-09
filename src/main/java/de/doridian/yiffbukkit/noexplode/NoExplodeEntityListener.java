package de.doridian.yiffbukkit.noexplode;

import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.ExplosionPrimedEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class NoExplodeEntityListener extends EntityListener
{
	public static NoExplode plugin;
	public NoExplodeEntityListener(NoExplode instance)
	{
		plugin = instance;
	}

	public void onExplosionPrimed(ExplosionPrimedEvent event)
	{
		if (!plugin.explodetnt && event.getEntity().getClass().getName().contains("CraftTNTPrimed"))
		{
			event.setFire(false);
			event.setRadius(0);
		}
		else if (!plugin.damagecreeper && event.getEntity().getClass().getName().contains("CraftCreeper"))
		{
			event.setFire(false);
			event.setRadius(0);
		}
		else if (!plugin.explodeghast && event.getEntity().getClass().getName().contains("CraftFireball"))
		{
			event.setFire(false);
			event.setRadius(0);
		}
	}

	public void onEntityExplode(EntityExplodeEvent event)
	{
		if (!plugin.explodetnt && event.getEntity().getClass().getName().contains("CraftTNTPrimed"))
		{
			event.setCancelled(true);
		}
		else if (!plugin.explodecreeper && event.getEntity().getClass().getName().contains("CraftCreeper"))
		{
			event.setCancelled(true);
		}
		else if (!plugin.explodeghast && event.getEntity().getClass().getName().contains("CraftFireball"))
		{
			event.setCancelled(true);
		}
	}
}
