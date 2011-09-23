package de.doridian.yiffbukkit.listeners;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityLiving;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.*;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EndermanPickupEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;

import de.doridian.yiffbukkit.YiffBukkit;

public class YiffBukkitEntityListener extends EntityListener {
	private final YiffBukkit plugin;

	public YiffBukkitEntityListener(YiffBukkit instance) {
		plugin = instance;

		PluginManager pm = plugin.getServer().getPluginManager();
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, this, Priority.Highest, plugin);
		pm.registerEvent(Event.Type.ENTITY_DEATH, this, Priority.Highest, plugin);
		pm.registerEvent(Event.Type.ENTITY_TARGET, this, Priority.Highest, plugin);
		pm.registerEvent(Event.Type.CREATURE_SPAWN, this, Priority.Highest, plugin);
		pm.registerEvent(Event.Type.ENDERMAN_PICKUP, this, Priority.Highest, plugin);
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
		monsterMap.put(CraftWolf.class, "a §9wolf§f");
		monsterMap.put(CraftSlime.class, "a §9slime§f");
		monsterMap.put(CraftGhast.class, "a §9ghast§f");
	}

	@Override
	public void onEndermanPickup(EndermanPickupEvent event) {		
		//Oh no you don't!
		event.setCancelled(true);
		
		//Yay Endermen now dupe items!
		Block block = event.getBlock();
		MaterialData data = new MaterialData(block.getType(), block.getData());
		CraftEnderman enderman = (CraftEnderman)event.getEntity();
		enderman.setCarriedMaterial(data);
	}
	
	@Override
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (event.getCreatureType() == CreatureType.SLIME)
			event.setCancelled(true);
	}

	@Override
	public void onEntityDamage(EntityDamageEvent event) {
		final Entity ent = event.getEntity();

		if (!(ent instanceof Player))
			return;

		final Player ply = (Player)ent;

		final String deathMessage;

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

			if (damager == null) {
				deathMessage = "§c%s§f was killed.";
				break;
			}

			String damagerName = monsterMap.get(damager.getClass());
			if (damagerName == null) {
				if (damager instanceof Player)
					damagerName = ((Player) damager).getName();
				else
					damagerName = "§9"+damager.toString()+"§f";
			}

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
		final Entity ent = event.getEntity();

		if (!(ent instanceof Player))
			return;

		final Player ply = (Player)ent;

		String deathMessage = "§c%s§f died.";

		final String playerName = ply.getName();
		if (ply.getLocation().getY() < -10D)
			deathMessage = "§c%s§f dug too deep.";
		else if (lastAttacker.containsKey(playerName))
			deathMessage = lastAttacker.get(playerName);

		lastAttacker.remove(playerName);
		plugin.ircbot.sendToChannel(playerName + " died.");
		plugin.getServer().broadcastMessage(String.format(deathMessage, playerName));
	}

	@Override
	public void onEntityTarget(EntityTargetEvent event) {
		final Entity entTarget = event.getTarget();

		if (!(entTarget instanceof Player))
			return;

		final Player target = (Player)entTarget;

		if (!plugin.vanish.vanishedPlayers.contains(target.getName()))
			return;

		if (event.getReason() != TargetReason.CLOSEST_PLAYER) {
			event.setCancelled(true);
			return;
		}
		final Entity mob = event.getEntity();
		final Vector mobPos = mob.getLocation().toVector();
		Player newTarget = null;
		double minDistanceSquared = 256.0D; // 16^2

		for (Player player : plugin.getServer().getOnlinePlayers()) {
			if (plugin.vanish.vanishedPlayers.contains(player.getName()))
				continue;

			EntityLiving notchEntity = ((CraftLivingEntity)mob).getHandle();
			EntityHuman notchPlayer = ((CraftHumanEntity)player).getHandle();

			if (!notchEntity.b(notchPlayer))
				continue;

			final Vector playerPos = player.getLocation().toVector();

			final double distanceSquared = mobPos.distanceSquared(playerPos);

			if (distanceSquared >= minDistanceSquared)
				continue;

			minDistanceSquared = distanceSquared;
			newTarget = player;
		}

		if (newTarget == null) {
			event.setCancelled(true);
			return;
		}

		event.setTarget(newTarget);
	}
}
