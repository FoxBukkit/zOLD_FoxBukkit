package de.doridian.yiffbukkit.listeners;

import de.doridian.yiffbukkit.YiffBukkit;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityLiving;
import org.bukkit.craftbukkit.entity.CraftBlaze;
import org.bukkit.craftbukkit.entity.CraftCaveSpider;
import org.bukkit.craftbukkit.entity.CraftCreeper;
import org.bukkit.craftbukkit.entity.CraftEnderDragon;
import org.bukkit.craftbukkit.entity.CraftEnderman;
import org.bukkit.craftbukkit.entity.CraftGhast;
import org.bukkit.craftbukkit.entity.CraftGiant;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.entity.CraftMagmaCube;
import org.bukkit.craftbukkit.entity.CraftPigZombie;
import org.bukkit.craftbukkit.entity.CraftSilverfish;
import org.bukkit.craftbukkit.entity.CraftSkeleton;
import org.bukkit.craftbukkit.entity.CraftSlime;
import org.bukkit.craftbukkit.entity.CraftSpider;
import org.bukkit.craftbukkit.entity.CraftWolf;
import org.bukkit.craftbukkit.entity.CraftZombie;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class YiffBukkitEntityListener implements Listener {
	private final YiffBukkit plugin;

	public YiffBukkitEntityListener(YiffBukkit instance) {
		plugin = instance;

		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	Map<String, String> lastAttacker = new HashMap<String, String>();

	Map<Class<? extends Entity>, String> monsterMap = new HashMap<Class<? extends Entity>, String>();
	{
		monsterMap.put(CraftCreeper.class, "a \u00a79creeper\u00a7f");
		monsterMap.put(CraftGiant.class, "a \u00a79giant\u00a7f");
		monsterMap.put(CraftSkeleton.class, "a \u00a79skeleton\u00a7f");
		monsterMap.put(CraftSpider.class, "a \u00a79spider\u00a7f");
		monsterMap.put(CraftZombie.class, "a \u00a79zombie\u00a7f");
		monsterMap.put(CraftPigZombie.class, "a \u00a79pig zombie\u00a7f");
		monsterMap.put(CraftWolf.class, "a \u00a79wolf\u00a7f");
		monsterMap.put(CraftSlime.class, "a \u00a79slime\u00a7f");
		monsterMap.put(CraftGhast.class, "a \u00a79ghast\u00a7f");
		monsterMap.put(CraftEnderman.class, "an \u00a79enderman\u00a7f");
		monsterMap.put(CraftSilverfish.class, "a \u00a79silverfish\u00a7f");
		monsterMap.put(CraftCaveSpider.class, "a \u00a79cave spider\u00a7f");
		monsterMap.put(CraftEnderDragon.class, "an \u00a79ender dragon\u00a7f");
		monsterMap.put(CraftBlaze.class, "a \u00a79blaze\u00a7f");
		monsterMap.put(CraftMagmaCube.class, "a \u00a79lava slime\u00a7f");
	}

	@EventHandler(event = CreatureSpawnEvent.class, priority = EventPriority.HIGHEST)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (event.getCreatureType() == CreatureType.SLIME)
			event.setCancelled(true);
	}

	@EventHandler(event = EntityDeathEvent.class, priority = EventPriority.HIGHEST)
	public void onEntityDeath(EntityDeathEvent event) {
		final Entity ent = event.getEntity();

		if (!(ent instanceof Player))
			return;

		final Player ply = (Player)ent;


		final String playerName = ply.getName();
		
		EntityDamageEvent damageEvent = ent.getLastDamageCause();
		final String deathMessage;
		switch (damageEvent.getCause()) {
		case BLOCK_EXPLOSION:
			deathMessage = "\u00a7c%s\u00a7f exploded.";
			break;

		case CONTACT:
			deathMessage = "\u00a7c%s\u00a7f is not a fakir.";
			break;

		case DROWNING:
			deathMessage = "\u00a7c%s\u00a7f can't hold breath for 10 minutes.";
			break;

		case ENTITY_ATTACK: {
			if (!(damageEvent instanceof EntityDamageByEntityEvent)) {
				deathMessage = "\u00a7c%s\u00a7f was killed.";
				break;
			}

			EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent)damageEvent;
			Entity damager = edbee.getDamager();

			if (damager == null) {
				deathMessage = "\u00a7c%s\u00a7f was killed.";
				break;
			}

			String damagerName = monsterMap.get(damager.getClass());
			if (damagerName == null) {
				if (damager instanceof Player)
					damagerName = ((Player) damager).getName();
				else
					damagerName = "\u00a79"+damager.toString()+"\u00a7f";
			}

			deathMessage = "\u00a7c%s\u00a7f was killed by "+damagerName+".";
			break;
		}

		case ENTITY_EXPLOSION:
			deathMessage = "\u00a7c%s\u00a7f exploded.";
			break;

		case FALL:
			deathMessage = "\u00a7c%s\u00a7f cratered.";
			break;

		case FIRE:
		case FIRE_TICK:
			deathMessage = "\u00a7c%s\u00a7f played with fire.";
			break;

		case LAVA:
			deathMessage = "\u00a7c%s\u00a7f went looking for diamonds in the wrong place.";
			break;

		case LIGHTNING:
			deathMessage = "\u00a7c%s\u00a7f angered the gods.";
			break;

		case PROJECTILE:
			deathMessage = "\u00a7c%s\u00a7f was not fast enough.";
			break;

		case SUICIDE:
			deathMessage = "\u00a7c%s\u00a7f brought shame to family.";
			break;

		case SUFFOCATION:
			deathMessage = "\u00a7c%s\u00a7f got a Mafia funeral.";
			break;

		case VOID:
			deathMessage = "\u00a7c%s\u00a7f dug too deep.";
			break;

		default:
			deathMessage = "\u00a7c%s\u00a7f died.";
		}

		lastAttacker.remove(playerName);
		plugin.ircbot.sendToChannel(playerName + " died.");
		((PlayerDeathEvent) event).setDeathMessage(String.format(deathMessage, playerName));
	}

	@EventHandler(event = EntityTargetEvent.class, priority = EventPriority.HIGHEST)
	public void onEntityTarget(EntityTargetEvent event) {
		final Entity entTarget = event.getTarget();

		if (!(entTarget instanceof Player))
			return;

		final Player target = (Player)entTarget;

		if (!plugin.vanish.isVanished(target))
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
			if (plugin.vanish.isVanished(player))
				continue;

			EntityLiving notchEntity = ((CraftLivingEntity)mob).getHandle();
			EntityHuman notchPlayer = ((CraftHumanEntity)player).getHandle();

			if (!notchEntity.g(notchPlayer))
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
