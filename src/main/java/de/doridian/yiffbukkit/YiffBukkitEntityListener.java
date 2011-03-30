package de.doridian.yiffbukkit;

import java.util.HashMap;
import java.util.Map;


import org.bukkit.craftbukkit.entity.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;

public class YiffBukkitEntityListener extends EntityListener {
	private final YiffBukkit plugin;

	public YiffBukkitEntityListener(YiffBukkit instance) {
		plugin = instance;

		PluginManager pm = plugin.getServer().getPluginManager();
		pm.registerEvent(Event.Type.ENTITY_DAMAGED, this, Priority.Highest, plugin);
		pm.registerEvent(Event.Type.ENTITY_DEATH, this, Priority.Highest, plugin);
		pm.registerEvent(Event.Type.ENTITY_TARGET, this, Priority.Highest, plugin);
	}

	Map<String, String> lastAttacker = new HashMap<String, String>();

	Map<Class<? extends Entity>, String> monsterMap = new HashMap<Class<? extends Entity>, String>();
	{
		monsterMap.put(CraftCreeper.class, "a §9creeper§f");
		monsterMap.put(CraftGiant.class, "a §9giant§f");
		monsterMap.put(CraftSkeleton.class, "a §9skeleton§f");
		monsterMap.put(CraftSpider.class, "a §9spider§f");
		monsterMap.put(CraftZombie.class, "a §9zombie§f");
		monsterMap.put(CraftPigZombie.class, "a §9pig zombie§f");
	}

	@Override
	public void onEntityDamage(EntityDamageEvent event) {
		Entity ent = event.getEntity();

		if (!(ent instanceof Player))
			return;

		Player ply = (Player)ent;

		String deathMessage;

		final DamageCause cause = event.getCause();
		switch (cause) {
		case BLOCK_EXPLOSION:
			deathMessage = "§c%s§f exploded.";
			break;

		case CONTACT:
			deathMessage = "§c%s§f is not a fakir.";
			break;

		case DROWNING:
			deathMessage = "§c%s§f can't hold breath for 10 minutes.";
			break;

		case ENTITY_ATTACK: {
			if (!(event instanceof EntityDamageByEntityEvent)) {
				deathMessage = "§c%s§f was killed.";
				break;
			}

			EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent)event;
			Entity damager = edbee.getDamager();

			String damagerName = monsterMap.get(damager.getClass());
			if (damagerName == null)
				damagerName = "§9"+damager.toString()+"§f";

			deathMessage = "§c%s§f was killed by "+damagerName+".";
			break;
		}

		case ENTITY_EXPLOSION:
			deathMessage = "§c%s§f exploded.";
			break;

		case FALL:
			deathMessage = "§c%s§f cratered.";
			break;

		case FIRE:
		case FIRE_TICK:
			deathMessage = "§c%s§f played with fire.";
			break;

		case LAVA:
			deathMessage = "§c%s§f went looking for diamonds in the wrong place.";
			break;

		case SUFFOCATION:
			deathMessage = "§c%s§f got a Mafia funeral.";
			break;

		default:
			deathMessage = "§c%s§f died.";
		}

		lastAttacker.put(ply.getName(), deathMessage);
	}

	@Override
	public void onEntityDeath(EntityDeathEvent event) {
		Entity ent = event.getEntity();

		if (!(ent instanceof Player))
			return;

		Player ply = (Player)ent;

		String deathMessage = "§c%s§f died.";

		if (ply.getLocation().getY() < -10D)
			deathMessage = "§c%s§f dug too deep.";
		else if (lastAttacker.containsKey(ply.getName()))
			deathMessage = lastAttacker.get(ply.getName());

		plugin.getServer().broadcastMessage(String.format(deathMessage, ply.getName()));
	}

	@Override
	public void onEntityTarget(EntityTargetEvent event) {
		Entity entTarget = event.getTarget();

		if (!(entTarget instanceof Player))
			return;

		Player target = (Player)entTarget;

		if (!plugin.playerHelper.vanishedPlayers.contains(target.getName()))
			return;
		final Entity mob = event.getEntity();
		Vector mobPos = mob.getLocation().toVector();
		Player newTarget = null;
		double minDistanceSquared = 256.0D; // 16^2

		for (Player player : plugin.getServer().getOnlinePlayers()) {
			if (plugin.playerHelper.vanishedPlayers.contains(player.getName()))
				continue;

			// TODO: check pig zombie aggression etc

			Vector playerPos = player.getLocation().toVector();

			double distanceSquared = mobPos.distanceSquared(playerPos);

			if (distanceSquared >= minDistanceSquared)
				continue;

			minDistanceSquared = distanceSquared;
			newTarget = player;
		}

		event.setTarget(newTarget);
	}
}
